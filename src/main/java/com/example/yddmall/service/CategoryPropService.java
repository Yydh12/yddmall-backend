package com.example.yddmall.service;

import com.example.yddmall.entity.CategoryProp;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 类目与属性的关联表 服务类
 * </p>
 *
 * @author Yy
 * @since 2025-09-15
 */
public interface CategoryPropService extends IService<CategoryProp> {

    List<CategoryProp> getCategoryPropByCid(Long id);

}
