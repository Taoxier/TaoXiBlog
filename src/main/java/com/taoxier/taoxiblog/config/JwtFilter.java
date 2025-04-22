package com.taoxier.taoxiblog.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description ：JWT请求过滤器
 * @Author taoxier
 * @Date 2025/4/21
 */
public class JwtFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException{
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse)  servletResponse;

        //后台管理路径外的请求直接跳过
        if (!request.getRequestURL().startsWith(request.getContextPath()+"/admin")){
            filterChain.doFilter(request,servletResponse);
            return;
        }
        String jwt=request.getHeader("Authorization");
        if (JwtUtis.judgeTokenIsExist(jwt)){

        }
    }



}
