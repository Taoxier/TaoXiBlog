package com.taoxier.taoxiblog.util;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description ：ip记录
 * @Author taoxier
 * @Date 2025/4/22
 */
@Slf4j
@Component
public class IpAddressUtils {

    /**
    * @Description 在Nginx等代理之后获取用户真实IP地址
     * @param request
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    public static String getIpAddress(HttpServletRequest request){
        //获取请求头（header）为X-Real-IP的值
        String ip = request.getHeader("X-Real-IP");
        if (ip==null || ip.length()==0||"unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // 获取客户端的 IP 地址
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip)||"0:0:0:0:0:0:0:1".equals(ip)){
                //根据网卡取本机配置的ip
                InetAddress inetAddress=null;
                try {
                    //获取本机的实际 IP 地址，并赋值给 ip 变量
                    inetAddress=InetAddress.getLocalHost();
                }catch (UnknownHostException e){
                    log.error("getIpAddress exception:",e);
                }
                ip=inetAddress.getHostAddress();
            }
        }
        return StringUtils.substringBefore(ip,",");
    }

    private static Searcher searcher;
    private static Method method;

    /**
    * @Description 在服务启动时加载 ip2region.db 到内存中,解决打包jar后找不到 ip2region.db 的问题
     * @param
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: void
     * @throws Exception 出现异常应该直接抛出终止程序启动，避免后续invoke时出现更多错误
    */
    //在服务启动时自动执行
    @PostConstruct
    private void initIp2regionResource() throws Exception{
        InputStream inputStream = new ClassPathResource("/ipdb/ip2region.xdb").getInputStream();
        //1.将 ip2region.xdb 转为 ByteArray
        byte[] dbBinStr= FileCopyUtils.copyToByteArray(inputStream);
        //2.使用上述的 dbBinStr 创建一个完全基于内存的查询对象。
        searcher=new Searcher(null,null,dbBinStr);
        //3.二进制方式初始化 DBSearcher，需要使用基于内存的查找算法 memorySearch
        method=searcher.getClass().getMethod("search",String.class);
    }

    /**
    * @Description 根据ip从 ip2region.db 中获取地理位置
     * @param ip
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: java.lang.String
    */
    public static String getCityInfo(String ip){
        try {
            String ipInfo=(String) method.invoke(searcher,ip);
            if (!StringUtils.isEmpty(ipInfo)){
                /*
                分别将 ipInfo 字符串中的 "|0" 和 "0|" 子字符串替换为空字符串
                 */
                ipInfo=ipInfo.replace("|0","");
                ipInfo=ipInfo.replace("0|","");
            }
            return ipInfo;
        }catch (Exception e){
            log.error("getCityInfo exception:",e);
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        IpAddressUtils ipAddressUtils=new IpAddressUtils();
        ipAddressUtils.initIp2regionResource();
        String cityInfo=ipAddressUtils.getCityInfo("14.215.177.39");
        System.out.println(cityInfo);
    }
}
