package com.ls.friendmatch.common;

/**
 * 返回工具类
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @param <T>
     * @return
     */
    public  static <T> BaseResponse<T> success (T data){
        return new BaseResponse(0,data,"ok") ;
    }

    /**
     * 错误
     * @param errorCode
     * @param <T>
     * @return
     */
    public  static <T> BaseResponse<T> error (ErrorCode errorCode){
        return new BaseResponse<>(errorCode) ;
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @param <T>
     * @return
     */
    public  static <T> BaseResponse<T> error (int code,String message,String description){
        return new BaseResponse<>(code,null,message,description) ;
    }

    /**
     * 失败
     * @param errCode
     * @param message
     * @param description
     * @param <T>
     * @return
     */
    public  static <T> BaseResponse<T> error (ErrorCode errCode,String message,String description){
        return new BaseResponse<>(errCode.getCode(),null,message,description) ;
    }

    /**
     * 失败
     * @param errCode
     * @param description
     * @param <T>
     * @return
     */
    public  static <T> BaseResponse<T> error (ErrorCode errCode ,String description){
        return new BaseResponse<T>(errCode.getCode(),errCode.getMessage(),description) ;
    }




}
