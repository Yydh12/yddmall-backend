package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.FavoriteMerchant;
import com.example.yddmall.entity.Merchant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FavoriteMerchantMapper extends BaseMapper<FavoriteMerchant> {

    @Select("SELECT COUNT(1) FROM favorite_merchant WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT m.* FROM favorite_merchant f JOIN merchant m ON f.merchant_id = m.merchant_id WHERE f.user_id = #{userId} ORDER BY f.create_time DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<Merchant> selectMerchantsByUser(@Param("userId") Long userId,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(1) FROM favorite_merchant WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    long existsByUserAndMerchant(@Param("userId") Long userId, @Param("merchantId") Long merchantId);

    @Select("SELECT COUNT(1) FROM favorite_merchant WHERE merchant_id = #{merchantId}")
    long countByMerchantId(@Param("merchantId") Long merchantId);
}
