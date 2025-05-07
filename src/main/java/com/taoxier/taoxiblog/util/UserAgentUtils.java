package com.taoxier.taoxiblog.util;

import com.taoxier.taoxiblog.model.dto.UserAgentDTO;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

/**
 * @Description ：UserAgent解析工具类
 * @Author taoxier
 * @Date 2025/4/22
 */
@Component
public class UserAgentUtils {
    private UserAgentAnalyzer uaa;

    public UserAgentUtils() {
        this.uaa = UserAgentAnalyzer
                .newBuilder()
                .useJava8CompatibleCaching()  //启用与 Java 8 兼容的缓存机制，有助于提高解析效率，避免重复解析相同的 User-Agent 字符
                .withCache(10000)  //设置缓存的大小为 10000 个条目
                .hideMatcherLoadStats()  //隐藏匹配器加载的统计信息，减少不必要的日志输出
                /*
                指定需要解析的字段，分别是客户端操作系统的名称、版本和主要版本信息，以及浏览器的名称和版本信息。
                 */
                .withField(UserAgent.OPERATING_SYSTEM_NAME_VERSION_MAJOR)
                .withField(UserAgent.AGENT_NAME_VERSION)
                .build();
    }

    /**
    * @Description 从User-Agent解析客户端操作系统和浏览器版本
     * @param userAgent
    * @Author: taoxier
    * @Date: 2025/4/22
    * @Return: UserAgentDTO
    */
    public UserAgentDTO parseOsAndBrowser(String userAgent) {
        UserAgent agent = uaa.parse(userAgent);
        String os = agent.getValue(UserAgent.OPERATING_SYSTEM_NAME_VERSION_MAJOR);
        String browser = agent.getValue(UserAgent.AGENT_NAME_VERSION);
        return new UserAgentDTO(os, browser);
    }
}
