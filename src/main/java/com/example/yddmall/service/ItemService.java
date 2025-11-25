package com.example.yddmall.service;

import com.example.yddmall.entity.Item;
import com.example.yddmall.vo.ItemSkuVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品基础信息表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-09-18
 */
public interface ItemService extends IService<Item> {

    /**
     * 新增商品基础信息
     * @param item 商品基础信息
     * @return 商品基础信息
     */
    Item saveItem(Item item);

    Page<ItemSkuVO> pageItemSku(Page<Item> page, Item item);

    /**
     * 新品上架（按上架时间）分页查询
     */
    Page<ItemSkuVO> pageNewest(Page<Item> page, Item item);

    /**
     * 热门商品（按销量聚合）分页查询
     */
    Page<ItemSkuVO> pagePopular(long current, long size, Long sellerId, Integer status, String title);

    /**
     * 根据商品id查询商品详情
     * @param id 商品id
     * @return 商品详情
     */
    ItemSkuVO getItem(Long id);

    /**
     * 批量按分类ID（cidList）分页查询商品SKU
     * 当传入cidList时优先使用IN条件过滤，不再使用单个cid精确匹配
     */
    Page<ItemSkuVO> pageItemSkuByCids(Page<Item> page, Item item, java.util.List<Long> cidList);

}
