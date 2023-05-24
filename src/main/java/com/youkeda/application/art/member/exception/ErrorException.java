package com.youkeda.application.art.member.exception;

import com.youkeda.application.art.member.model.Result;

/**
 * 通用的错误异常
 *
 * @author yingjunjiao
 */
public class ErrorException extends Exception {
    private static final long serialVersionUID = -5611360696489522466L;

    private String code;

    private String message;

    public ErrorException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    /**
     * 获取 result
     *
     * @param <T>
     * @return
     */
    public <T> Result<T> getResult() {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 设置 result
     *
     * @param result
     */
    public void setResult(Result result) {
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
    }

}
