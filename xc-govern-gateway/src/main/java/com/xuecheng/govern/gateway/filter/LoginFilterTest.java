package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Liucheng
 * @date 2019/4/25 17:49
 */
// @Component
public class LoginFilterTest extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilterTest.class);

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
        RequestContext requestContext = RequestContext.getCurrentContext();

        // 得到request
        HttpServletRequest request = requestContext.getRequest();

        // 得到response
        HttpServletResponse response = requestContext.getResponse();

        // 获取Authorization头
        String authorization = request.getHeader("Authorization");

        if (StringUtils.isEmpty(authorization)) {

            // 拒绝访问: 将不会继续执行后面的过滤器，会直接被返回
            requestContext.setSendZuulResponse(false);

            // 设置响应代码
            requestContext.setResponseStatusCode(200);

            // 构建响应信息
            ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);

            // 转换成json，设置contentType
            String jsonString = JSON.toJSONString(responseResult);
            requestContext.setResponseBody(jsonString);
            response.setContentType("application/json;charset=utf-8");

        }
        return null;
    }
}
