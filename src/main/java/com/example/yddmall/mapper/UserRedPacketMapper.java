package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.UserRedPacket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRedPacketMapper extends BaseMapper<UserRedPacket> {

    @Select("SELECT COUNT(*) FROM user_red_packet WHERE red_packet_id = #{redPacketId} AND user_id = #{userId}")
    int countUserClaims(@Param("redPacketId") Long redPacketId, @Param("userId") Long userId);
}