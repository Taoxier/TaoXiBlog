package com.taoxier.taoxiblog.task;

import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.service.BlogService;
import com.taoxier.taoxiblog.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @Author taoxier
 * @Date 2025/5/19 下午2:38
 * @描述  Redis相关定时任务
 */
@Component
public class RedisSyncScheduleTask {
    @Autowired
    RedisService redisService;
    @Autowired
    BlogService blogService;

    /**
    * @描述  从Redis同步博客文章浏览量到数据库
    * @param
    * @return void
    * @Author taoxier
    */
    public void syncBlogViewsToDatabase() {
        String redisKey = RedisKeyConstants.BLOG_VIEWS_MAP;
        Map blogViewsMap = redisService.getMapByKey(redisKey);
        Set<Integer> keys = blogViewsMap.keySet();
        for (Integer key : keys) {
            Integer views = (Integer) blogViewsMap.get(key);
            blogService.updateBlogViews(key.longValue(), views);
        }
    }
}
