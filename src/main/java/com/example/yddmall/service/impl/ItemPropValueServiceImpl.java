package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.entity.ItemPropValue;
import com.example.yddmall.mapper.ItemPropValueMapper;
import com.example.yddmall.service.ItemPropValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品属性值定义表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-09-15
 */
@Service
public class ItemPropValueServiceImpl extends ServiceImpl<ItemPropValueMapper, ItemPropValue> implements ItemPropValueService {

    private final ItemPropValueMapper itemPropValueMapper;

    public ItemPropValueServiceImpl(ItemPropValueMapper itemPropValueMapper) {
        this.itemPropValueMapper = itemPropValueMapper;
    }

    @Override
    public List<ItemPropValue> getItemPropByPid(Long id) {
        return itemPropValueMapper.selectList(new QueryWrapper<ItemPropValue>().eq("pid", id));
    }
}
