package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获类: 注意，要在自动类上注解扫描此类所在的包
 * 使用
 *  @ControllerAdvice
 *  @ExceptionHandler
 *  注解来捕获指定类型的异常
 */
@ControllerAdvice
public class ExceptionCatch {

    /**
     * 日志处理
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    /**
     * 使用EXCEPTIONS存放异常类型和错误代码的映射，
     * ImmutableMap的特点是一旦 被【构建】，不可改变，而且线程安全
     */
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;

    /**
     * 使用builder来【构建】一个异常类型和错误代码的异常，就是上方的 EXCEPTIONS
     */
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    /**
     * 在这里加入一些基础的异常类型进行判断
     */
    static {
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
        builder.put(HttpRequestMethodNotSupportedException.class, CommonCode.INVALID_PARAM);
    }

    /**
     * 捕获自行定的 CustomException 异常
     * 并将信息返回给前端 ResponseBody 表示回复字符串
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException e) {
        LOGGER.error("catch exception : {} \r\nexception: ", e.getMessage(), e);
        ResultCode resultCode = e.getResultCode();
        ResponseResult responseResult = new ResponseResult(resultCode);

        return responseResult;
    }

    /**
     * 处理 框架或者第三方抛出的异常，使用一个 构建map(一旦被构建，只能读，不能写)存放可能出现的
     * 情况！
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e) {
        // 记录日志
        LOGGER.error("catch exception : {} \r\nexception: ", e.getMessage(), e);

        if (EXCEPTIONS == null) {
            // 如果这个Map为空，则使用构建者开始构建
            EXCEPTIONS = builder.build();
        }

        final ResultCode resultCode = EXCEPTIONS.get(e.getClass());
        final ResponseResult responseResult;

        if (resultCode != null) {
            responseResult = new ResponseResult(resultCode);
        } else {
            responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
        }

        return responseResult;
    }
}
