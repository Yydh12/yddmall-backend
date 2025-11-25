package com.example.yddmall.service.impl;

import com.example.yddmall.entity.ItemSku;
import com.example.yddmall.mapper.ItemSkuMapper;
import com.example.yddmall.service.ItemSkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 商品SKU信息表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-09-18
 */
@Service
public class ItemSkuServiceImpl extends ServiceImpl<ItemSkuMapper, ItemSku> implements ItemSkuService {

    @Override
    public List<ItemSku> saveItemSku(List<ItemSku> itemSkuList) {
        // 1. 确保所有 SKU 都有外键
        Long itemId = itemSkuList.get(0).getItemId();
        itemSkuList.forEach(sku -> sku.setItemId(itemId));

        // 2. 批量插入（ServiceImpl 自带批量方法）
        this.saveBatch(itemSkuList);   // ✅ 一条 SQL 插多条
        return itemSkuList;
    }
}
