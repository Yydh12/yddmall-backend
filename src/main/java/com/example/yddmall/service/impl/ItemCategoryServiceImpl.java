package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.entity.ItemCategory;
import com.example.yddmall.mapper.ItemCategoryMapper;
import com.example.yddmall.service.ItemCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品类目表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-07-31
 */
@Service
public class ItemCategoryServiceImpl extends ServiceImpl<ItemCategoryMapper, ItemCategory> implements ItemCategoryService {

    @Override
    public List<ItemCategory> getParentcid(int level) {
        QueryWrapper<ItemCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_cid", level);
        return baseMapper.selectList(queryWrapper);
    }
}
