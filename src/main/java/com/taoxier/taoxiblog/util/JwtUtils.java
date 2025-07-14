package com.taoxier.taoxiblog.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/21
 */
@Component
public class JwtUtils {
    /*
    存储 JWT的过期时间，以毫秒为单位
     */
    private static long expireTime;

    /*
    存储 JWT 的密钥
     */
    private static String secretKey;

    @Value("abcdefghijklmnopqrstuvwxyz")
    public void setSecretKey(String secretKey) {
        JwtUtils.secretKey = secretKey;
    }

    @Value("259200000") //即2.5天
    public void setExpireTime(long expireTime) {
        JwtUtils.expireTime = expireTime;
    }

    /**
     * @param token
     * @Description 判断token是否存在
     * @Author: taoxier
     * @Date: 2025/4/21
     * @Return: boolean
     */
    public static boolean judgeTokenIsExist(String token) {
        return token != null && !"".equals(token) && !"null".equals(token);
    }

    /**
    * @Description 生成token
     * @param subject
    * @Author: taoxier
    * @Date: 2025/4/21
    * @Return: java.lang.String
    */
    public static String generateToken(String subject) {
        String jwt = Jwts.builder()
                .setSubject(subject) //设置主题声明
                .setExpiration(new Date(System.currentTimeMillis() + expireTime)) //设置过期时间
                .signWith(SignatureAlgorithm.HS512, secretKey) //设置签名算法和密钥
                .compact(); //生成 JWT
        return jwt;
    }

    /**
    * @Description 生成带角色权限的token
     * @param subject
     * @param authorities
    * @Author: taoxier
    * @Date: 2025/4/21
    * @Return: java.lang.String
    */
    public static String generateToken(String subject, Collection<? extends GrantedAuthority> authorities){
        //可变字符串StringBuilder
        StringBuilder sb=new StringBuilder();
        for (GrantedAuthority authority:authorities){
            //调用getAuthority() 方法获取权限的具体标识，每个权限标识之间用逗号, 分隔
            sb.append(authority.getAuthority()).append(",");
        }
        String jwt=Jwts.builder()
                .setSubject(subject)
                .claim("authorities",sb) //添加一个自定义声明（claim）
                .setExpiration(new Date(System.currentTimeMillis()+expireTime))
                .signWith(SignatureAlgorithm.HS512,secretKey)
                .compact();
        return jwt;
    }

    /**
    * @Description 生成自定义过期时间token
     * @param subject
     * @param expireTime
    * @Author: taoxier
    * @Date: 2025/4/21
    * @Return: java.lang.String
    */
    public static String generateToken(String subject,long expireTime){
        String jwt=Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis()+expireTime))
                .signWith(SignatureAlgorithm.HS512,secretKey)
                .compact();
        return jwt;
    }

    /**
    * @Description 获取tokenBody同时校验token是否有效（无效则会抛出异常）
     * @param token
    * @Author: taoxier
    * @Date: 2025/4/21
    * @Return: io.jsonwebtoken.Claims  //存储和获取 JWT 中的各种声明（claims）信息
    */
    public static Claims getTokenBody(String token){
        Claims claims=Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.replace("Bearer","")).getBody();
        return claims;
    }

}
