package com.taoxier.taoxiblog.util;

import cn.hutool.core.lang.hash.MurmurHash;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.security.DigestException;

/**
 * @Description ：Hash工具类
 * @Author taoxier
 * @Date 2025/4/22
 */
public class HashUtils {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();

    /**
    * @Description 获取输入字符串的 MD5 哈希值
     * @param str
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    private static String getMd5(CharSequence str){
        return DigestUtils.md5DigestAsHex(str.toString().getBytes());
    }

    /**
    * @Description 获取 MurmurHash 算法计算的 32 位哈希值
     * @param str
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: long
    */
    public static long getMurmurHash32(String str){
        int i= MurmurHash.hash32(str);
        //处理负数
        long num=i<0?Integer.MAX_VALUE-(long)i:i;
        return num;
    }

    /**
    * @Description 使用 BCryptPasswordEncoder 对输入的原始密码进行 bcrypt 加密
     * @param rawPassword
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    public  static String getBC(CharSequence rawPassword){
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    /**
    * @Description 比较输入的原始密码和已加密的密码是否匹配
     * @param rawPassword
     * @param encodedPassword
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: boolean
    */
    public static boolean matchBC(CharSequence rawPassword,String encodedPassword){
        return bCryptPasswordEncoder.matches(rawPassword,encodedPassword);
    }

    public static void main(String[] args) {
        System.out.println(getBC("123456"));
    }

}
