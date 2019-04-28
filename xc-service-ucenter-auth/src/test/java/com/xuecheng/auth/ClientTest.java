package com.xuecheng.auth;

import com.sun.jersey.core.util.Base64;
import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * 申请令牌测试，outh2 已经集成在本服务中；远程调用，其实还是访问的本服务自己
 * @author Liucheng
 * @date 2019/4/24 18:57
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClientTest {

    /**
     * Feign 负载均衡客户端
     */
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    /**
     * Java 客户端 请求令牌测试
     */
    @Test
    public void testClient() {
        // 采用客户端负载均衡，从eureka获取认证服务的ip端口,其实就是自己所在的服务
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);

        URI uri = serviceInstance.getUri();

        String authUrl = uri + "/auth/oauth/token";

        // 1、header信息，包括了http basic认证信息
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();

        // "Basic WGNXZWJBcHA6WGNXZWJBcHA="
        String httpBasic = this.httpBasic("XcWebApp", "XcWebApp");

        header.add("Authorization", httpBasic);

        // 2. body 部分 包括：grant_type、username、password
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", "lingting");
        body.add("password", "abc123");


        /**
         (URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
         url就是 申请令牌的url /oauth/token
         method http的方法类型
         requestEntity请求内容
         responseType，将响应的结果生成的类型

         请求的内容分两部分 header body
         */
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(body, header);

        // 指定restTemplate 遇到400 或者401时响应，不要抛出异常
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        // 远程调用申请令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, multiValueMapHttpEntity, Map.class);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(exchange.getBody());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }

    private String httpBasic(String clientId, String clientSecret) {
        // 将客户端id和客户端密码拼接起来，格式 “客户端id:客户端密码”
        String msg = clientId + ":" + clientSecret;

        // 进行base64编码
        byte[] encode = Base64.encode(msg.getBytes());

        return "Basic " + new String(encode);
    }















}
