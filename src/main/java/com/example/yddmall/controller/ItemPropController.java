package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.service.ItemPropService;
import com.example.yddmall.entity.ItemProp;
import com.example.yddmall.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

@RestController
@RequestMapping("/item-prop")
@Tag(name = "商品属性定义表接口")
public class ItemPropController {

    private final ItemPropService itemPropService;

    public ItemPropController(ItemPropService itemPropService){
        this.itemPropService = itemPropService;
    }

    //分页查询
    @GetMapping
    public Page<ItemProp> page(Page<ItemProp> page, ItemProp itemProp) {
        return itemPropService.page(page, new QueryWrapper<>(itemProp));
    }

    //通过id查询单条数据
    @GetMapping("/{id}")
    public ApiResponse<List<ItemProp>> getById(@PathVariable Long id) {
        return ResponseUtils.success(itemPropService.getItemPropByPid(id));
    }

    //新增数据
    @PostMapping
    public boolean save(@RequestBody ItemProp itemProp) {
        return itemPropService.save(itemProp);
    }

    //修改数据
    @PutMapping
    public boolean updateById(@RequestBody ItemProp itemProp) {
        return itemPropService.updateById(itemProp);
    }

    //删除数据
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return itemPropService.removeById(id);
    }
}
