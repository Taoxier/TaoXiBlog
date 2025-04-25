package com.taoxier.taoxiblog.config;

import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.util.JacksonUtils;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, org.springframework.security.core.AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        ResultVO result = ResultVO.create(403, "请登录");
        out.write(JacksonUtils.writeValueAsString(result));
        out.flush();
        out.close();
    }
}
