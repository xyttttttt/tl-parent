package com.xyt.ssyx.common.exception;

import com.xyt.ssyx.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//AOP  面向切面
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)  //出现对应的异常会执行这个方法  异常处理器
    @ResponseBody     //返回
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail(null);
    }

    //自定义异常处理
    @ExceptionHandler(SsyxException.class)
    @ResponseBody
    public Result error(SsyxException exception){
        return Result.build(null,exception.getCode(),exception.getMessage());
    }
}

