package com.example.yddmall.service;

import com.example.yddmall.entity.ItemProp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品属性定义表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-09-15
 */
public interface ItemPropService extends IService<ItemProp> {

    List<ItemProp> getItemPropByPid(Long id);

}
