package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.User;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 商城权限用户表 Mapper 接口
 * </p>
 *
 * @author Yy
 * @since 2025-07-31
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询以ydd为前缀的用户名中数字部分最大的记录
     * @return 最大数字部分的用户名
     */
    @Select("SELECT username FROM user " +
            "WHERE username LIKE 'ydd%' " +
            "AND username REGEXP '^ydd[0-9]+$' " +
            "ORDER BY CAST(SUBSTRING(username, 4) AS UNSIGNED) DESC " +
            "LIMIT 1")
    String selectMaxYddUsername();

}

