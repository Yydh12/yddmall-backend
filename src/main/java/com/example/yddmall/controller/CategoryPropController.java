package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.service.CategoryPropService;
import com.example.yddmall.entity.CategoryProp;
import com.example.yddmall.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/category-prop")
@Tag(name = "类目与属性的关联表接口")
public class CategoryPropController {

    private final CategoryPropService categoryPropService;

    public CategoryPropController(CategoryPropService categoryPropService){
        this.categoryPropService = categoryPropService;
    }

    //分页查询
    @GetMapping
    public Page<CategoryProp> page(Page<CategoryProp> page, CategoryProp categoryProp) {
        return categoryPropService.page(page, new QueryWrapper<>(categoryProp));
    }

    //通过id查询单条数据
    @GetMapping("/{id}")
    public ApiResponse<CategoryProp> getById(@PathVariable Long id) {
        return ResponseUtils.success(categoryPropService.getById(id));
    }

    //新增数据
    @PostMapping
    public boolean save(@RequestBody CategoryProp categoryProp) {
        return categoryPropService.save(categoryProp);
    }

    //修改数据
    @PutMapping
    public boolean updateById(@RequestBody CategoryProp categoryProp) {
        return categoryPropService.updateById(categoryProp);
    }

    //删除数据
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return categoryPropService.removeById(id);
    }
}
