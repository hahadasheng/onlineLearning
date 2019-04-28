package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Liucheng
 * @date 2019/4/24 20:36
 */
@RestController
public class AuthController implements AuthControllerApi {

    @Value("${auth.clientId}")
    String clientId;

    @Value("${auth.clientSecret}")
    String clientSecret;

    @Value("${auth.cookieDomain}")
    String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Autowired
    AuthService authService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        // 校验账号是否输入
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }

        // 校验密码是否输入
        if (StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }

        AuthToken authToken = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), clientId, clientSecret);

        // 访问token
        String accessToken = authToken.getAccess_token();

        // 将令牌写入cookie
        saveCookie(accessToken);
        return new LoginResult(CommonCode.SUCCESS, accessToken);

    }

    /**
     * 将令牌写入到cookie中
     * @param token
     */
    private void saveCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getResponse();

        // 添加cookie 认证令牌，最后一个参数设置为false，表示允许浏览器获取
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
        CookieUtil.addCookie(response, "localhost", "/", "uid", token, cookieMaxAge, false);
    }

    /**
     * 查询userjwt令牌
     * @return
     */
    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {
        // 获取cookie中的令牌
        String access_token = this.getTokenFormCookie();

        // 根据令牌从redis中查询jwt
        AuthToken userToken = authService.getUserToken(access_token);

        if (userToken == null) {
            return new JwtResult(CommonCode.FAIL, null);
        }



        return new JwtResult(CommonCode.SUCCESS, userToken.getJwt_token());
    }

    /**
     * 从cookie中读取访问令牌
     * @return
     */
    private String getTokenFormCookie() {
        // HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        return cookieMap.get("uid");
    }

    /**
     * 用户退出功能
     * @return
     */
    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        // 取出身份令牌
        String token = getTokenFormCookie();

        // 删除redis中token
        authService.delToken(token);

        // 清除cookie
        this.clearCookie(token);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 清除cookie
     * @param token
     */
    private void clearCookie(String token) {
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }

    /**
     * 添加header
     * @param token
     */
    private void addHeader(String token) {
        response.setHeader("Authorization", "Bearer " + token);
    }

}
