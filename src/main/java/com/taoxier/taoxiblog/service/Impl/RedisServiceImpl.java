package com.taoxier.taoxiblog.service.Impl;

import com.taoxier.taoxiblog.model.vo.BlogInfoVO;
import com.taoxier.taoxiblog.model.vo.PageResultVO;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.util.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate jsonRedisTemplate;

    /**
    * @Description  Redis 里指定哈希键下对应页码的博客信息分页结果
     * @param hash
     * @param pageNum
    * @Author: taoxier
    * @Date: 2025/4/25
    * @Return: com.taoxier.taoxiblog.model.vo.PageResultVO<com.taoxier.taoxiblog.model.vo.BlogInfoVO>
    */
    @Override
    public PageResultVO<BlogInfoVO> getBlogInfoPageResultHash(String hash , Integer pageNum){
        if (jsonRedisTemplate.opsForHash().hasKey(hash,pageNum)){
            Object redisResult= jsonRedisTemplate.opsForHash().get(hash,pageNum);
            PageResultVO<BlogInfoVO> pageResult= JacksonUtils.convertValue(redisResult,PageResultVO.class);
            return pageResult;
        }else {
            return null;
        }
    }

    @Override
    public void saveKVToHash(String hash,Object key,Object value){
        jsonRedisTemplate.opsForHash().put(hash,key,value);
    }

    @Override
    public void saveMapToHash(String hash, Map map){
        jsonRedisTemplate.opsForHash().putAll(hash,map);
    }

    @Override
    public Object getValueByHashKey(String hash,Object key){
        return jsonRedisTemplate.opsForHash().get(hash,key);
    }

    @Override
    public void deleteByHashKey(String hash,Object key){
        jsonRedisTemplate.opsForHash().delete(hash,key);
    }

    @Override
    public <T> List<T> getListByKey(String key){
        List<T> redisResult=(List<T>) jsonRedisTemplate.opsForValue().get(key);
        return redisResult;
    }

    @Override
    public  <T> void saveListToValue(String key,List<T> list){
        jsonRedisTemplate.opsForValue().set(key,list);
    }

    @Override
    public <T> Map<String,T> getMapByKey(String key){
        Map<String,T> redisResult=(Map<String, T>) jsonRedisTemplate.opsForValue().get(key);
        return redisResult;
    }

    @Override
    public <T> void saveMapToValue(String key,Map<String,T> map){
        jsonRedisTemplate.opsForValue().set(key,map);
    }

    @Override
    public <T> T getObjectByKey(String key,Class t){
        Object redisResult= jsonRedisTemplate.opsForValue().get(key);
        T object=(T)JacksonUtils.convertValue(redisResult,t);
        return object;
    }

    /**
     * @Description 递增
     * @param hash
     * @param key
     * @param increment
     * @Author: taoxier
     * @Date: 2025/4/25
     * @Return: void
     */
    @Override
    public void incrementByHashKey(String hash,Object key,int increment){
        if (increment<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        jsonRedisTemplate.opsForHash().increment(hash,key,increment);
    }

    @Override
    public void incrementByKey(String key,int increment){
        if (increment < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        jsonRedisTemplate.opsForValue().increment(key,increment);
    }

    @Override
    public void saveObjectToValue(String key, Object object) {
        jsonRedisTemplate.opsForValue().set(key, object);
    }

    @Override
    public void saveValueToSet(String key, Object value) {
        jsonRedisTemplate.opsForSet().add(key, value);
    }

    @Override
    public int countBySet(String key) {
        return jsonRedisTemplate.opsForSet().size(key).intValue();
    }

    @Override
    public void deleteValueBySet(String key, Object value) {
        jsonRedisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public boolean hasValueInSet(String key, Object value) {
        return jsonRedisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public void deleteCacheByKey(String key) {
        jsonRedisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return jsonRedisTemplate.hasKey(key);
    }

    @Override
    public void expire(String key, long time) {
        jsonRedisTemplate.expire(key, time, TimeUnit.SECONDS);
    }
}
