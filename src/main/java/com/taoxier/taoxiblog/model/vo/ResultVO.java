package com.taoxier.taoxiblog.model.vo;

import lombok.*;

/**
 * @Description ：封装响应结果
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
public class ResultVO {
    private Integer code;
    private String msg;
    private Object data;


    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResultVO ok(String msg, Object data) {
        return new ResultVO(200, msg, data);
    }

    public static ResultVO ok(String msg) {
        return new ResultVO(200, msg);
    }

    public static ResultVO error(String msg) {
        return new ResultVO(500, msg);
    }

    public static ResultVO error() {
        return new ResultVO(500, "异常错误");
    }

    public static ResultVO create(Integer code, String msg, Object data) {
        return new ResultVO(code, msg, data);
    }

    public static ResultVO create(Integer code, String msg) {
        return new ResultVO(code, msg);
    }
}
