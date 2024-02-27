package com.ls.friendmatch.Exception;

import com.ls.friendmatch.common.ErrorCode;

/**
 * 自定义异常类
 */
public class BusinessException extends  RuntimeException{

    /**
     * 异常码
     */
    private final int code  ;

    /**
     * 描述
     */
    private final String description ;

    public BusinessException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode,String description) {
        super(description);
        this.code = errorCode.getCode();
        this.description = description;
    }

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
