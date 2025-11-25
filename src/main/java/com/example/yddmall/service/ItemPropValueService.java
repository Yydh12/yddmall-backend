package com.example.yddmall.service;

import com.example.yddmall.entity.ItemPropValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品属性值定义表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-09-15
 */
public interface ItemPropValueService extends IService<ItemPropValue> {

    List<ItemPropValue> getItemPropByPid(Long id);

}
