package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.entity.CategoryProp;
import com.example.yddmall.entity.ItemProp;
import com.example.yddmall.entity.ItemPropValue;
import com.example.yddmall.mapper.ItemPropMapper;
import com.example.yddmall.service.ItemPropService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品属性定义表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-09-15
 */
@Service
public class ItemPropServiceImpl extends ServiceImpl<ItemPropMapper, ItemProp> implements ItemPropService {

    private final ItemPropMapper itemPropMapper;

    public ItemPropServiceImpl(ItemPropMapper itemPropMapper) {
        this.itemPropMapper = itemPropMapper;
    }

    @Autowired
    private CategoryPropServiceImpl categoryPropService;

    @Autowired
    private ItemPropValueServiceImpl itemPropValueService;

    @Override
    public List<ItemProp> getItemPropByPid(Long id) {
        List<CategoryProp> categoryProps = categoryPropService.getCategoryPropByCid(id);

        List<ItemProp> itemProps = new ArrayList<>();

        for (CategoryProp categoryProp : categoryProps) {
            // 获取每个分类属性对应的商品属性（属性名）
            ItemProp itemProp = itemPropMapper.selectOne(
                    new QueryWrapper<ItemProp>().eq("pid", categoryProp.getPid())
            );
            if (itemProp != null) {
                // 获取该属性对应的所有属性值
                List<ItemPropValue> propValues = itemPropValueService.getItemPropByPid(itemProp.getPid());

                itemProp.setItemPropValue(propValues);
                itemProps.add(itemProp);
            }
        }

        return itemProps;
    }
}
