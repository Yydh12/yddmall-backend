package com.example.yddmall.service;

import com.example.yddmall.entity.ItemCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品类目表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-07-31
 */
public interface ItemCategoryService extends IService<ItemCategory> {

    List<ItemCategory> getParentcid(int level);

}
