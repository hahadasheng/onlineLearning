package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Liucheng
 * @date 2019/4/24 18:36
 */
@Api(value = "用户认证", description = "用户认证接口")
public interface AuthControllerApi {

    @ApiOperation("登陆")
    public LoginResult login(LoginRequest loginRequest);

    @ApiOperation("退出登陆")
    public ResponseResult logout();

    @ApiOperation("查询userjwt；令牌")
    public JwtResult userjwt();
}
