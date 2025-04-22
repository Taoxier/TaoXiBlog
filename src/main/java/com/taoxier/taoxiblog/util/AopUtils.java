package com.taoxier.taoxiblog.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Description ：AOP工具类
 * @Author taoxier
 * @Date 2025/4/22
 */
public class AopUtils {
    private static Set<String> ignoreParams = new HashSet<String>() {
        {
            add("jwt");
        }
    };


    /**
    * @Description 获取请求参数
     * @param joinPoint
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.util.Map<java.lang.String,java.lang.Object>
    */
    public static Map<String, Object> getRequestParams(JoinPoint joinPoint) {
        Map<String, Object> map = new LinkedHashMap<>();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (!isIgnoreParams(parameterNames[i]) && !isFilterObject(args[i])) {
                map.put(parameterNames[i], args);
            }
        }
        return map;
    }

    /**
     * @param o
     * @Description 考虑数据是否是文件、HttpServletRequest 或 HttpServletResponse
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: boolean
     */
    private static boolean isFilterObject(final Object o) {
        /*
        instanceof 用于判断一个对象是否是某个类（或接口）的实例
         */
        return o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof MultipartFile;
    }


    /**
     * @param params
     * @Description 判断是否忽略参数(如果 ignoreParams 集合中包含 params ， 则 isIgnoreParams 方法返回 true ， 表示该参数应该被忽略 ； 否则返回 false)
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: boolean
     */
    private static boolean isIgnoreParams(String params) {
        return ignoreParams.contains(params);
    }
}
