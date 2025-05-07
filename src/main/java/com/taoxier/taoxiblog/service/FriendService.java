package com.taoxier.taoxiblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.taoxier.taoxiblog.model.dto.FriendDTO;
import com.taoxier.taoxiblog.model.entity.Friend;
import com.taoxier.taoxiblog.model.vo.FriendInfo;
import com.taoxier.taoxiblog.model.vo.FriendVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/25
 */
public interface FriendService extends IService<Friend> {
    List<Friend> getFriendList();

    List<FriendVO> getFriendVOList();

    @Transactional(rollbackFor = Exception.class)
    void updateFriendPublishedById(Long friendId, Boolean published);

    @Transactional(rollbackFor = Exception.class)
    void saveFriend(Friend friend);

    @Transactional(rollbackFor = Exception.class)
    void updateFriend(FriendDTO friend);

    @Transactional(rollbackFor = Exception.class)
    void deleteFriend(Long id);

    @Transactional(rollbackFor = Exception.class)
    void updateViewsByNickname(String nickname);

    FriendInfo getFriendInfo(boolean cache, boolean md);

    @Transactional(rollbackFor = Exception.class)
    void updateFriendInfoContent(String content);

    @Transactional(rollbackFor = Exception.class)
    void updateFriendInfoCommentEnabled(Boolean commentEnabled);

    void deleteFriendInfoRedisCache();
}
