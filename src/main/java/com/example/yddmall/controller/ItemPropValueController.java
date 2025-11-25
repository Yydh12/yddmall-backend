package com.example.yddmall.controller;

import com.example.yddmall.service.ItemPropValueService;
import com.example.yddmall.entity.ItemPropValue;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/item-prop-value")
@Tag(name = "商品属性值定义表接口")
public class ItemPropValueController {

    private final ItemPropValueService itemPropValueService;

    public ItemPropValueController(ItemPropValueService itemPropValueService){
        this.itemPropValueService = itemPropValueService;
    }

    //分页查询
    @GetMapping
    public Page<ItemPropValue> page(Page<ItemPropValue> page, ItemPropValue itemPropValue) {
        return itemPropValueService.page(page, new QueryWrapper<>(itemPropValue));
    }

    //通过id查询单条数据
    @GetMapping("/{id}")
    public ItemPropValue getById(@PathVariable Long id) {
        return itemPropValueService.getById(id);
    }

    //新增数据
    @PostMapping
    public boolean save(@RequestBody ItemPropValue itemPropValue) {
        return itemPropValueService.save(itemPropValue);
    }

    //修改数据
    @PutMapping
    public boolean updateById(@RequestBody ItemPropValue itemPropValue) {
        return itemPropValueService.updateById(itemPropValue);
    }

    //删除数据
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return itemPropValueService.removeById(id);
    }
}
