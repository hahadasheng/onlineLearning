package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常类，处理已知的异常
 */
public class CustomException extends RuntimeException{

    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        // 异常信息为 错误代码 + 异常信息

        super("错误代码：" + resultCode.code() + " 错误信息：" + resultCode.message());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }
}
