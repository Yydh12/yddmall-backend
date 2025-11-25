package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
    
    /**
     * 根据用户ID查询地址列表
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId} ORDER BY is_default DESC, create_time DESC")
    List<Address> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的默认地址
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Address selectDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 取消用户的所有默认地址
     */
    @Select("UPDATE address SET is_default = 0 WHERE user_id = #{userId}")
    void cancelDefaultByUserId(@Param("userId") Long userId);
}