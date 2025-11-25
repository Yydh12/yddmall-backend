package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.yddmall.entity.Item;
import com.example.yddmall.mapper.ItemMapper;
import com.example.yddmall.mapper.OrderItemMapper;
import com.example.yddmall.service.ItemService;
import com.example.yddmall.utils.GenerateUniqueNoUtils;
import com.example.yddmall.vo.ItemSkuVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品基础信息表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-09-18
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    private GenerateUniqueNoUtils generateUniqueNoUtils;

    private final ItemMapper itemMapper;
    private final OrderItemMapper orderItemMapper;

    public ItemServiceImpl(ItemMapper itemMapper, OrderItemMapper orderItemMapper) {
        this.itemMapper = itemMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public Item saveItem(Item item) {
        // 生成商品编号
        if (item.getItemNo() == null) {
            String itemNo = generateUniqueNoUtils.generateItemNo();
            item.setItemNo(itemNo);
        }
        
        baseMapper.insert(item);
        System.out.println(item);
        return item;
    }

    @Override
    public Page<ItemSkuVO> pageItemSku(Page<Item> page, Item item) {
        // 1. 手动构造 Wrapper，只做模糊查询
        QueryWrapper<Item> qw = new QueryWrapper<>();

        
        // 按店铺主账号ID精准过滤（店铺维度查询）
        if (item.getSellerId() != null) {
            qw.eq("seller_id", item.getSellerId());
        }
        
        // 按商品ID精准查询
        if (item.getItemId() != null) {
            qw.eq("item_id", item.getItemId());
        }
        
        // 按状态查询
        if (item.getStatus() != null) {
            qw.eq("status", item.getStatus());
        }
        // 按分类ID精确查询（支持按末级类目cid筛选）
        if (item.getCid() != null) {
            qw.eq("cid", item.getCid());
        }
        
        // 模糊查询条件
        if (StringUtils.isNotBlank(item.getTitle())) {
            qw.like("title", item.getTitle());          // 标题模糊
        }
        if (StringUtils.isNotBlank(item.getSubTitle())) {
            qw.like("sub_title", item.getSubTitle());   // 副标题模糊
        }
        if (StringUtils.isNotBlank(item.getBrand())) {
            qw.like("brand", item.getBrand());          // 品牌模糊
        }
        if (StringUtils.isNotBlank(item.getItemNo())) {
            qw.like("item_no", item.getItemNo());       // 商品编号模糊
        }

        // 排序条件：最后更新时间倒序
        qw.orderByDesc("last_update");

        // 2. 分页查询
        Page<Item> itemPage = itemMapper.selectPage(page, qw);

        // 3. PO -> VO 转换
        List<ItemSkuVO> voList = itemPage.getRecords().stream().map(i -> {
            ItemSkuVO vo = new ItemSkuVO();
            vo.setItemId(i.getItemId());
            vo.setItemNo(i.getItemNo());
            vo.setTitle(i.getTitle());
            vo.setSubtitle(i.getSubTitle());
            vo.setBrand(i.getBrand());
            vo.setPrice(i.getPrice());
            vo.setQuantity(0);  // 默认0，后续可以从库存服务查询
            // 处理图片URL，取第一张作为SKU图片
            vo.setSkuPic(i.getPicUrl() == null || i.getPicUrl().isEmpty()
                    ? null
                    : i.getPicUrl().split(",")[0]);
            vo.setStatus(i.getStatus().intValue());
            vo.setSellerId(i.getSellerId().intValue());
            vo.setSalesCount(0L);
            return vo;
        }).collect(Collectors.toList());

        // 4. 组装返回
        Page<ItemSkuVO> voPage = new Page<>();
        BeanUtils.copyProperties(itemPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<ItemSkuVO> pageItemSkuByCids(Page<Item> page, Item item, java.util.List<Long> cidList) {
        QueryWrapper<Item> qw = new QueryWrapper<>();

        // 店铺维度
        if (item.getSellerId() != null) {
            qw.eq("seller_id", item.getSellerId());
        }
        // 单个商品ID
        if (item.getItemId() != null) {
            qw.eq("item_id", item.getItemId());
        }
        // 状态
        if (item.getStatus() != null) {
            qw.eq("status", item.getStatus());
        }

        // 批量分类ID优先（若提供cidList则不再使用单个cid）
        if (cidList != null && !cidList.isEmpty()) {
            qw.in("cid", cidList);
        } else if (item.getCid() != null) {
            qw.eq("cid", item.getCid());
        }

        // 模糊条件
        if (StringUtils.isNotBlank(item.getTitle())) {
            qw.like("title", item.getTitle());
        }
        if (StringUtils.isNotBlank(item.getSubTitle())) {
            qw.like("sub_title", item.getSubTitle());
        }
        if (StringUtils.isNotBlank(item.getBrand())) {
            qw.like("brand", item.getBrand());
        }
        if (StringUtils.isNotBlank(item.getItemNo())) {
            qw.like("item_no", item.getItemNo());
        }

        // 排序：更新时间倒序
        qw.orderByDesc("last_update");

        Page<Item> itemPage = itemMapper.selectPage(page, qw);

        java.util.List<ItemSkuVO> voList = itemPage.getRecords().stream().map(i -> {
            ItemSkuVO vo = new ItemSkuVO();
            vo.setItemId(i.getItemId());
            vo.setItemNo(i.getItemNo());
            vo.setTitle(i.getTitle());
            vo.setSubtitle(i.getSubTitle());
            vo.setBrand(i.getBrand());
            vo.setPrice(i.getPrice());
            vo.setQuantity(0);
            vo.setSkuPic(i.getPicUrl() == null || i.getPicUrl().isEmpty() ? null : i.getPicUrl().split(",")[0]);
            vo.setStatus(i.getStatus().intValue());
            vo.setSellerId(i.getSellerId().intValue());
            vo.setSalesCount(0L);
            return vo;
        }).collect(java.util.stream.Collectors.toList());

        Page<ItemSkuVO> voPage = new Page<>();
        org.springframework.beans.BeanUtils.copyProperties(itemPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<ItemSkuVO> pageNewest(Page<Item> page, Item item) {
        QueryWrapper<Item> qw = new QueryWrapper<>();
        if (item.getSellerId() != null) {
            qw.eq("seller_id", item.getSellerId());
        }
        if (item.getItemId() != null) {
            qw.eq("item_id", item.getItemId());
        }
        if (item.getStatus() != null) {
            qw.eq("status", item.getStatus());
        }
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(item.getTitle())) {
            qw.like("title", item.getTitle());
        }
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(item.getSubTitle())) {
            qw.like("sub_title", item.getSubTitle());
        }
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(item.getBrand())) {
            qw.like("brand", item.getBrand());
        }
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(item.getItemNo())) {
            qw.like("item_no", item.getItemNo());
        }
        // 按上架时间倒序
        qw.orderByDesc("list_time");

        Page<Item> itemPage = itemMapper.selectPage(page, qw);
        java.util.List<ItemSkuVO> voList = itemPage.getRecords().stream().map(i -> {
            ItemSkuVO vo = new ItemSkuVO();
            vo.setItemId(i.getItemId());
            vo.setItemNo(i.getItemNo());
            vo.setTitle(i.getTitle());
            vo.setSubtitle(i.getSubTitle());
            vo.setBrand(i.getBrand());
            vo.setPrice(i.getPrice());
            vo.setQuantity(0);
            vo.setSkuPic(i.getPicUrl() == null || i.getPicUrl().isEmpty() ? null : i.getPicUrl().split(",")[0]);
            vo.setStatus(i.getStatus().intValue());
            vo.setSellerId(i.getSellerId().intValue());
            vo.setSalesCount(0L);
            return vo;
        }).collect(java.util.stream.Collectors.toList());

        Page<ItemSkuVO> voPage = new Page<>();
        org.springframework.beans.BeanUtils.copyProperties(itemPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<ItemSkuVO> pagePopular(long current, long size, Long sellerId, Integer status, String title) {
        // 1. 先查销量聚合分页
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.example.yddmall.vo.PopularItemStatVO> statPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.core.metadata.IPage<com.example.yddmall.vo.PopularItemStatVO> stats =
                orderItemMapper.selectPopularItems(statPage, sellerId, status, title);

        java.util.List<Long> itemIds = stats.getRecords().stream()
                .map(com.example.yddmall.vo.PopularItemStatVO::getItemId)
                .collect(java.util.stream.Collectors.toList());

        java.util.Map<Long, Long> salesMap = new java.util.HashMap<>();
        for (com.example.yddmall.vo.PopularItemStatVO s : stats.getRecords()) {
            salesMap.put(s.getItemId(), s.getSalesCount());
        }

        java.util.List<Item> items = itemIds.isEmpty()
                ? java.util.Collections.emptyList()
                : itemMapper.selectList(new QueryWrapper<Item>().in("item_id", itemIds));
        // 维持顺序
        java.util.Map<Long, Item> itemMap = new java.util.HashMap<>();
        for (Item it : items) {
            itemMap.put(it.getItemId(), it);
        }

        java.util.List<ItemSkuVO> voList = new java.util.ArrayList<>();
        for (Long id : itemIds) {
            Item i = itemMap.get(id);
            if (i == null) continue;
            ItemSkuVO vo = new ItemSkuVO();
            vo.setItemId(i.getItemId());
            vo.setItemNo(i.getItemNo());
            vo.setTitle(i.getTitle());
            vo.setSubtitle(i.getSubTitle());
            vo.setBrand(i.getBrand());
            vo.setPrice(i.getPrice());
            vo.setQuantity(0);
            vo.setSkuPic(i.getPicUrl() == null || i.getPicUrl().isEmpty() ? null : i.getPicUrl().split(",")[0]);
            vo.setStatus(i.getStatus().intValue());
            vo.setSellerId(i.getSellerId().intValue());
            vo.setSalesCount(salesMap.getOrDefault(id, 0L));
            voList.add(vo);
        }

        Page<ItemSkuVO> voPage = new Page<>(current, size);
        voPage.setTotal(stats.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ItemSkuVO getItem(Long id) {
        // ItemSku itemSku = itemSkuService.getById(id);
        // if (itemSku == null) {
        //     return null;
        // }
        ItemSkuVO vo = new ItemSkuVO();
        // BeanUtils.copyProperties(item, vo);
        return vo;
    }
}
