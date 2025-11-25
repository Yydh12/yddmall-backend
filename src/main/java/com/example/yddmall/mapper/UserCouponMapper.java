package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    @Select("SELECT COUNT(*) FROM user_coupon WHERE coupon_id = #{couponId} AND user_id = #{userId}")
    int countUserClaims(@Param("couponId") Long couponId, @Param("userId") Long userId);
}