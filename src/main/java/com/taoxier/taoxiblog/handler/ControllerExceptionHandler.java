package com.taoxier.taoxiblog.handler;

import com.taoxier.taoxiblog.exception.NotFoundException;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description ：对Controller层全局异常处理
 * @RestControllerAdvice 捕获异常后，返回json数据类型
 * @Author taoxier
 * @Date 2025/4/22
 */
//全局异常处理和全局数据绑定
@RestControllerAdvice
public class ControllerExceptionHandler {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    /**
    * @Description 捕获自定义的404异常
     * @param request  请求
     * @param e 自定义抛出的异常信息
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    public ResultVO notFoundExceptionHandler(HttpServletRequest request, NotFoundException e){
        logger.error("Request URL :{},Exception :",request.getRequestURI(),e);
        return ResultVO.create(404,e.getMessage());
    }

    /**
    * @Description 捕获自定义的持久化异常
     * @param request
     * @param e
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @ExceptionHandler(PersistenceException.class)
    public ResultVO persistenceExceptionHandler(HttpServletRequest request, PersistenceException e) {
        logger.error("Request URL : {}, Exception :", request.getRequestURL(), e);
        return ResultVO.create(500, e.getMessage());
    }

    /**
    * @Description 捕获自定义的登录失败异常
     * @param request
     * @param e
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResultVO usernameNotFoundExceptionHandler(HttpServletRequest request, UsernameNotFoundException e) {
        logger.error("Request URL : {}, Exception :", request.getRequestURL(), e);
        return ResultVO.create(401, "用户名或密码错误！");
    }

    /**
    * @Description 捕获其它异常
     * @param request
     * @param e
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: com.taoxier.taoxiblog.model.vo.ResultVO
    */
    @ExceptionHandler(Exception.class)
    public ResultVO exceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("Request URL : {}, Exception :", request.getRequestURL(), e);
        return ResultVO.create(500, "异常错误");
    }
}
