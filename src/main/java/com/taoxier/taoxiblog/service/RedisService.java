package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.vo.BlogInfoVO;
import com.taoxier.taoxiblog.model.vo.PageResultVO;

import java.util.List;
import java.util.Map;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface RedisService {
    PageResultVO<BlogInfoVO> getBlogInfoPageResultHash(String hash, Integer pageNum);

    void saveKVToHash(String hash, Object key, Object value);

    void saveMapToHash(String hash, Map map);

    Map getMapByHash(String hash);

    Object getValueByHashKey(String hash, Object key);

    void deleteByHashKey(String hash, Object key);

    <T> List<T> getListByKey(String key);

    <T> void saveListToValue(String key, List<T> list);

    <T> Map<String,T> getMapByKey(String key);

    <T> void saveMapToValue(String key, Map<String, T> map);

    <T> T getObjectByKey(String key, Class t);

    void incrementByHashKey(String hash, Object key, int increment);

    void incrementByKey(String key, int increment);

    void saveObjectToValue(String key, Object object);

    void saveValueToSet(String key, Object value);

    int countBySet(String key);

    void deleteValueBySet(String key, Object value);

    boolean hasValueInSet(String key, Object value);

    void deleteCacheByKey(String key);

    boolean hasKey(String key);

    void expire(String key, long time);
}
