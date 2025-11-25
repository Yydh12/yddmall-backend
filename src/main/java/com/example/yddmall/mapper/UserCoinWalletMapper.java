package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.UserCoinWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserCoinWalletMapper extends BaseMapper<UserCoinWallet> {

    @Select("SELECT * FROM user_coin_wallet WHERE user_id = #{userId} LIMIT 1")
    UserCoinWallet findByUserId(@Param("userId") Long userId);
}