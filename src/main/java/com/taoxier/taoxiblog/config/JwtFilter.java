package com.taoxier.taoxiblog.config;

import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.util.JacksonUtils;
import com.taoxier.taoxiblog.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @Description ：JWT请求过滤器
 * @Author taoxier
 * @Date 2025/4/21
 */
public class JwtFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //后台管理路径外的请求直接跳过
        if (!request.getRequestURI().startsWith(request.getContextPath() + "/admin")) {
            filterChain.doFilter(request, servletResponse);
            return;
        }
        //从请求的 Authorization 头中获取 JWT 令牌。
        String jwt = request.getHeader("Authorization");

        //  验证 JWT 并处理
        if (JwtUtils.judgeTokenIsExist(jwt)) {
            try {
                Claims claims = JwtUtils.getTokenBody(jwt);
                String username = claims.getSubject();
                //将权限信息转换为 List<GrantedAuthority> 列表
                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
                //设置到 SecurityContextHolder 中，表示用户已认证
                SecurityContextHolder.getContext().setAuthentication(token);
            } catch (Exception e) {
                e.printStackTrace();
                response.setContentType("application/json;charset=utf-8");
                ResultVO result = ResultVO.create(403, "凭证已失效，请重新登录！");
                PrintWriter out = response.getWriter();
                out.write(JacksonUtils.writeValueAsString(result));
                out.flush();
                out.close();
                return;
            }
        }
        //如果 JWT 验证通过或请求不是访问后台管理路径，将请求继续传递给过滤器链中的下一个过滤器或目标资源
        filterChain.doFilter(servletRequest, servletResponse);
    }


}
