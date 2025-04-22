package com.taoxier.taoxiblog.util.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description ：获取 Spring 应用上下文中的 Bean 以及相关的操作
 * @Author taoxier
 * @Date 2025/4/22
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext=applicationContext;
    }

    /**
    * @Description 获取对应的 Bean 实例
     * @param name
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.Object
    */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    /**
     * @Description 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型
     * @param requiredType
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: T
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
    * @Description 获取指定类型的 Bean 实例
     * @param name
     * @param requiredType
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: T
    */
    public static <T> T getBean(String name,Class<T> requiredType){
        return applicationContext.getBean(name,requiredType);
    }

    /**
    * @Description 判断应用上下文中是否存在指定名称 name 的 Bean
     * @param name
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: boolean
    */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
    * @Description 判断指定名称 name 的 Bean 是否为单例模式
     * @param name
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: boolean
    */
    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    /**
    * @Description 获取指定名称 name 的 Bean 的类型
     * @param name
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.Class<? extends java.lang.Object>
    */
    public static Class<? extends Object> getType(String name) {
        return applicationContext.getType(name);
    }

}
