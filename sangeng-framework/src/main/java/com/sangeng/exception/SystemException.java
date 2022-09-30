package com.sangeng.exception;

import com.sangeng.enums.AppHttpCodeEnum;

/**
 * 设置自定义异常，controller层中方法可能抛出来的异常
 */
public class SystemException extends RuntimeException{
    //错误状态码
    private int code;

    //错误信息
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }
}
