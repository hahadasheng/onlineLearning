package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Liucheng
 * @date 2019/4/24 19:33
 */
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    /**
     * redis中token的过期时间
     */
    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;

    /**
     * okhttp
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 客户端负载均衡
     */
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 由于令牌存储时采用 String序列化策略，所以这里用 StringRedisTemplate来查询存储，使用RedisTemplate无
     * 法完成查询。
     */
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 认证方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        // 申请令牌
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);

        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        // 将token存储到redis
        String accessToken = authToken.getAccess_token();

        String content = JSON.toJSONString(authToken);

        boolean saveTokenResult = saveToken(accessToken, content, tokenValiditySeconds);

        if (!saveTokenResult) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }

        return authToken;
    }


    /**
     * 认证方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {

        // 选中认证服务的地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);

        if (serviceInstance == null) {
            LOGGER.error("choose an auth instance fail");
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_AUTHSERVER_NOTFOUND);
        }

        // 获取令牌的url
        String path = serviceInstance.getUri().toString() + "/auth/oauth/token";

        // 定义body
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        // 授权方式
        formData.add("grant_type", "password");

        // 账号
        formData.add("username", username);

        // 密码
        formData.add("password", password);

        // 定义header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", httpBasic(clientId, clientSecret));

        // 指定restTemplate 遇到400 或者401时响应，不要抛出异常
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        Map map = null;

        try {
            // HTTP请求spring security的申请令牌接口
            ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData, header), Map.class);
            map = mapResponseEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("request oauth_token_password error: {}",e.getMessage());
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        if (map == null || map.get("access_token") == null
                || map.get("refresh_token") == null
                || map.get("jti") == null) {
            // jti是jwt令牌的唯一标识作为用户身份令牌,与之一一对应


            // 获取spring security返回的错误信息
            String error_description = (String) map.get("error_description");

            if (StringUtils.isNotEmpty(error_description)) {
                if (error_description.equals("坏的凭证")) {
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                } else if (error_description.indexOf("returned null") >= 0) {

                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }
            }

            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        AuthToken authToken = new AuthToken();

        // 访问令牌(jwt)
        String jwtToken = (String) map.get("access_token");

        // 刷新令牌
        String refreshToken = (String) map.get("refresh_token");

        // jti ,用户身份标识
        String accessToken = (String) map.get("jti");

        authToken.setJwt_token(jwtToken);
        authToken.setAccess_token(accessToken);
        authToken.setRefresh_token(refreshToken);

        return authToken;

    }

    /**
     * 获取httpbasic认证串
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String httpBasic(String clientId, String clientSecret) {
        // 将客户端id和客户端密码拼接起来，格式 “客户端id:客户端密码”
        String msg = clientId + ":" + clientSecret;

        // 进行base64编码
        byte[] encode = Base64.encode(msg.getBytes());

        return "Basic " + new String(encode);
    }


    /**
     * 存储令牌到redis缓存中
     * @param accessToken
     * @param content
     * @param ttl
     * @return
     */
    private boolean saveToken (String accessToken, String content, long ttl) {
        // 令牌名称
        String name = "user_token:" + accessToken;

        // 保存令牌到redis
        stringRedisTemplate.boundValueOps(name).set(content, ttl, TimeUnit.SECONDS);

        // 获取过期时间【由于上面保存的方法没有返回值，所以通过这种方式来判断令牌是否保存成功！】
        Long expire = stringRedisTemplate.getExpire(name);

        return expire > 0;
    }

    /**
     * 从redis查询令牌
     * @param token
     * @return
     */
    public AuthToken getUserToken(String token) {
        String userToken = "user_token:" + token;
        String userTokenString = stringRedisTemplate.opsForValue().get(userToken);

        if (userToken != null) {
            AuthToken authToken = null;

            try {
                authToken = JSON.parseObject(userTokenString, AuthToken.class);
            } catch (Exception e) {
                LOGGER.error("getUserToken from redis and execute JSON.parseObject error {}",e.getMessage());
            }

            return authToken;
        }
        return null;
    }

    /**
     * 从redis中删除令牌,由于设置了过期时间，告诉redis删除对应的token即可
     * @param access_token
     * @return
     */
    public boolean delToken(String access_token) {
        String name = "user_token:" + access_token;
        stringRedisTemplate.delete(name);
        return true;
    }

}
