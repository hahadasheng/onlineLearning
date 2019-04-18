package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 异常抛出类;方便程序中调用！
 */
public class ExceptionCast {

    /**
     * 使用静态方法抛出自定义异常
     * @param resultCode
     */
    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }


}
