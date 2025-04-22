package com.taoxier.taoxiblog.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Description ：字符串校验
 * @Author taoxier
 * @Date 2025/4/22
 */
public class StringUtils {

    /**
     * @param str
     * @Description 判空
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: boolean
     */
    public static boolean isEmpty(String... str) {
        for (String s : str) {
            if (s == null || "".equals(s.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param str
     * @Description 判断字符串中是否包含特殊字符
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: boolean
     */
    public static boolean hasSpecialChar(String... str) {
        for (String s : str) {
            if (s.contains("%") || s.contains("_") || s.contains("[") || s.contains("#") || s.contains("*")) {
                return true;
            }
        }
        return false;
    }

    public static String substring(String str, int start, int end) {
        if (str == null || "".equals(str)) {
            return "";
        }
        if (start < 0 || end < 0) {
            return str;
        }
        if (start >= end) {
            return "";
        }
        return str.substring(start, end);
    }

    /**
    * @Description 获取堆栈信息
     * @param throwable
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            //把异常的堆栈跟踪信息输出到 PrintWriter 中
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}
