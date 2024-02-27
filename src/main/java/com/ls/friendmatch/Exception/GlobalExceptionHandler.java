package com.ls.friendmatch.Exception;

import com.ls.friendmatch.common.BaseResponse;
import com.ls.friendmatch.common.ErrorCode;
import com.ls.friendmatch.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException e) {
        log.error("businessException" + e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription()) ;
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(BusinessException e) {
        log.error("RuntimeException" ,e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"") ;
    }
}
