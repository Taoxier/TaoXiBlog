package com.taoxier.taoxiblog.exception;

/**
 * @Description ：持久化异常
 * @Author taoxier
 * @Date 2025/4/22
 */
public class PersistenceException extends RuntimeException{
    public PersistenceException() {

    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
