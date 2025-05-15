package com.taoxier.taoxiblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.exception.PersistenceException;
import com.taoxier.taoxiblog.mapper.FriendMapper;
import com.taoxier.taoxiblog.mapper.SiteSettingMapper;
import com.taoxier.taoxiblog.model.dto.FriendDTO;
import com.taoxier.taoxiblog.model.entity.Friend;
import com.taoxier.taoxiblog.model.entity.SiteSetting;
import com.taoxier.taoxiblog.model.vo.FriendInfo;
import com.taoxier.taoxiblog.model.vo.FriendVO;
import com.taoxier.taoxiblog.service.FriendService;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.util.markdown.MarkdownUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {
    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @Autowired
    private RedisService redisService;

    /**
    * @Description 查询友链List
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.entity.Friend>
    */
    @Override
    public List<Friend> getFriendList() {
        return friendMapper.selectList(null);
    }

    /**
    * @Description 查询友链VO List
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: java.util.List<com.taoxier.taoxiblog.model.vo.FriendVO>
    */
    @Override
    public List<FriendVO> getFriendVOList() {
        return friendMapper.getFriendVOList();
    }

    /**
    * @Description 更新友链公开状态-
     * @param friendId
     * @param published
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFriendPublishedById(Long friendId, Boolean published) {
        Friend friend = new Friend();
        friend.setId(friendId);
        friend.setPublished(published);
        if (!updateById(friend)) {
            throw new PersistenceException("操作失败");
        }
    }

    /**
    * @Description 添加友链
     * @param friend
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveFriend(Friend friend) {
        friend.setViews(0);
        friend.setCreateTime(new Date());
        if (!save(friend)) {
            throw new PersistenceException("添加失败");
        }
    }

    /**
    * @Description 更新友链
     * @param friend
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFriend(FriendDTO friend) {
        Friend newFriend = new Friend();
        //使用 BeanUtils 复制属性
        BeanUtils.copyProperties(friend, newFriend);
        if (!updateById(newFriend)) {
            throw new PersistenceException("修改失败");
        }
    }

    /**
    * @Description 按id删除友链
     * @param id
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteFriend(Long id) {
        if (!removeById(id)) {
            throw new PersistenceException("删除失败");
        }
    }

    /**
    * @Description 增加友链浏览次数
     * @param nickname
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateViewsByNickname(String nickname) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("views = views + 1")
                .eq("nickname", nickname);
        int rows = friendMapper.update(null, updateWrapper);
        if (rows != 1) {
            throw new PersistenceException("操作失败");
        }
    }

    @Override
    public FriendInfo getFriendInfo(boolean cache, boolean md) {
        String redisKey = RedisKeyConstants.FRIEND_INFO_MAP;
        if (cache) {
            FriendInfo friendInfoFromRedis = redisService.getObjectByKey(redisKey, FriendInfo.class);
            if (friendInfoFromRedis != null) {
                return friendInfoFromRedis;
            }
        }
        QueryWrapper<SiteSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("type", 4);
        List<SiteSetting> siteSettings = siteSettingMapper.selectList(wrapper);
        FriendInfo friendInfo = new FriendInfo();
        for (SiteSetting siteSetting : siteSettings) {
            if ("friendContent".equals(siteSetting.getNameEn())) {
                if (md) {
                    friendInfo.setContent(MarkdownUtils.markdownToHtmlExtensions(siteSetting.getValue()));
                } else {
                    friendInfo.setContent(siteSetting.getValue());
                }
            } else if ("friendCommentEnabled".equals(siteSetting.getNameEn())) {
                if ("1".equals(siteSetting.getValue())) {
                    friendInfo.setCommentEnabled(true);
                } else {
                    friendInfo.setCommentEnabled(false);
                }
            }
        }
        if (cache && md) {
            redisService.saveObjectToValue(redisKey, friendInfo);
        }
        return friendInfo;
    }

    /**
    * @Description 更新友链页面信息
     * @param content
    * @Author: taoxier
    * @Date: 2025/5/8
    * @Return: void
    */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFriendInfoContent(String content) {
        QueryWrapper<SiteSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("name_en", "friendContent");
        SiteSetting siteSetting = new SiteSetting();
        siteSetting.setValue(content);
        if (siteSettingMapper.update(siteSetting, wrapper) != 1) {
            throw new PersistenceException("修改失败");
        }
        deleteFriendInfoRedisCache();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFriendInfoCommentEnabled(Boolean commentEnabled) {
        QueryWrapper<SiteSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("name_en", "friendCommentEnabled");
        SiteSetting siteSetting = new SiteSetting();
        siteSetting.setValue(commentEnabled ? "1" : "0");
        if (siteSettingMapper.update(siteSetting, wrapper) != 1) {
            throw new PersistenceException("修改失败");
        }
        deleteFriendInfoRedisCache();
    }

    /**
    * @Description 删除友链页面缓存
     * @param
    * @Author: taoxier
    * @Date: 2025/5/7
    * @Return: void
    */
    @Override
    public void deleteFriendInfoRedisCache() {
        redisService.deleteCacheByKey(RedisKeyConstants.FRIEND_INFO_MAP);
    }
}
