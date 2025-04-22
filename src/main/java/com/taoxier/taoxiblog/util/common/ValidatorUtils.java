package com.taoxier.taoxiblog.util.common;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @Description ：校验 Java 对象的属性是否符合预定义的约束条件
 * @Author taoxier
 * @Date 2025/4/22
 */
public class ValidatorUtils {
    /*
    用于执行对象的校验操作
     */
    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
    * @Description 校验对象
     * @param object 待校验对象
     * @param groups 待校验的组
     * @Throws RuntimeException 校验不通过，则报BusinessException异常
     * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
    */
    public static void validateEntity(Object object, Class<?>... groups) throws RuntimeException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            //从违反约束条件的集合中取出第一个违反约束的信息
            ConstraintViolation<Object> constraintViolation = constraintViolations.iterator().next();
            throw new RuntimeException(constraintViolation.getMessage());
        }

    }
}
