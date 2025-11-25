package com.example.yddmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yddmall.entity.Item;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 商品基础信息表 Mapper 接口
 * </p>
 *
 * @author Yy
 * @since 2025-09-18
 */
public interface ItemMapper extends BaseMapper<Item> {

    @Select("SELECT COUNT(1) FROM item WHERE seller_id = #{sellerId} AND status = 1")
    long countOnSaleBySeller(@Param("sellerId") Long sellerId);

}

