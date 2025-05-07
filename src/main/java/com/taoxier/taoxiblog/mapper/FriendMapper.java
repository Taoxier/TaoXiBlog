package com.taoxier.taoxiblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taoxier.taoxiblog.model.entity.Friend;
import com.taoxier.taoxiblog.model.vo.FriendVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description ：
 * @Author taoxier
 * @Date 2025/4/22
 */
@Mapper
@Repository
public interface FriendMapper extends BaseMapper<Friend> {


    List<FriendVO> getFriendVOList();
}