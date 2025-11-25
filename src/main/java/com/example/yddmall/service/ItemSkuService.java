package com.example.yddmall.service;

import com.example.yddmall.entity.ItemSku;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 商品SKU信息表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-09-18
 */
public interface ItemSkuService extends IService<ItemSku> {

    /**
     * 新增商品SKU信息
     * @param itemSku 商品SKU信息
     * @return 商品SKU信息
     */
    List<ItemSku> saveItemSku(List<ItemSku> itemSkuList);
}
