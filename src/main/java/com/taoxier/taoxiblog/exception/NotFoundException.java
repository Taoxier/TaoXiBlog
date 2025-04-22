package com.taoxier.taoxiblog.exception;

/**
 * @Description ：404异常
 * @Author taoxier
 * @Date 2025/4/22
 */
public class NotFoundException extends RuntimeException{
    public NotFoundException() {

    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
