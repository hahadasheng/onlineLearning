package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Liucheng
 * @date 2019/4/25 17:49
 */
@Component
public class LoginFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

    @Autowired
    private AuthService authService;

    /**
     * 过滤器的类型
     *      pre: 请求在被路由执行之前执行
     *      routing: 在路由请求时调用
     *      post: 在routing和error过滤器之后调用
     *      error: 处理请求时发生错误调用时执行
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器序号，越小越被优先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 标识此过滤器是否要执行，返回true则表示要执行
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的内容
     * 测试需求： 过滤所有请求，判断头部信息是否含有Authorization，如果没有则拒绝访问，否则转发到微服务。
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        // 上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();

        // 请求对象
        HttpServletRequest request = requestContext.getRequest();

        // 查询身份令牌
        String accessToken = authService.getTokenFromCookie(request);

        if (accessToken == null) {
            // 拒绝访问
            this.accessDenied();
        }

        // 从redis中校验身份令牌是否过期
        long expire = authService.getExpire(accessToken);

        if (expire <= 0 ) {
            // 拒绝访问
            this.accessDenied();
        }

        // 查询jwt令牌
        String jwt = authService.getJwtFromHeader(request);

        if (jwt == null) {
            // 拒绝访问
            this.accessDenied();
        }
        return null;
    }

    /**
     * 拒绝访问处理
     */
    private void accessDenied () {
        // 上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();

        // 拒绝访问
        requestContext.setSendZuulResponse(false);

        // 设置响应内容
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        String resultString = JSON.toJSONString(responseResult);

        requestContext.setResponseBody(resultString);

        HttpServletResponse response = requestContext.getResponse();
        response.setContentType("application/json;charset=utf-8");
    }
}
