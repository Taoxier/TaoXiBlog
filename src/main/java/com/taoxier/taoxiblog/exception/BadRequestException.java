package com.taoxier.taoxiblog.exception;

/**
 * @Description ：非法请求异常
 * @Author taoxier
 * @Date 2025/4/22
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException() {

    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
