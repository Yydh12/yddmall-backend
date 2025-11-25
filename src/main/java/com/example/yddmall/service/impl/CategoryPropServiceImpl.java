package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yddmall.entity.CategoryProp;
import com.example.yddmall.mapper.CategoryPropMapper;
import com.example.yddmall.service.CategoryPropService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 类目与属性的关联表 服务实现类
 * </p>
 *
 * @author Yy
 * @since 2025-09-15
 */
@Service
public class CategoryPropServiceImpl extends ServiceImpl<CategoryPropMapper, CategoryProp> implements CategoryPropService {

    private final CategoryPropMapper categoryPropMapper;

    public CategoryPropServiceImpl(CategoryPropMapper categoryPropMapper) {
        this.categoryPropMapper = categoryPropMapper;
    }

    @Override
    public List<CategoryProp> getCategoryPropByCid(Long id) {
        return categoryPropMapper.selectList(
                new QueryWrapper<CategoryProp>().eq("cid", id)
        );
    }
}