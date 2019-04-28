package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

/**
 * 在模块中自定义异常处理器,继承了common中自定义的异常处理，相当于在此基础上进行了扩展
 * @author Liucheng
 * @date 2019/4/26 13:20
 */
@ControllerAdvice
public class CustomExceptionCatch extends ExceptionCatch {

    static {
        // 除了CustomException以外的异常类型以及对应错误代码在这里定义，如果不统一另一则统一返回固定的错误信息
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }

}
