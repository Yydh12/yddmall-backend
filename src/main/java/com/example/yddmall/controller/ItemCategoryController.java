package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.service.ItemCategoryService;
import com.example.yddmall.entity.ItemCategory;
import com.example.yddmall.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item-category")
@Tag(name = "商品类目表接口")
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    public ItemCategoryController(ItemCategoryService itemCategoryService){
        this.itemCategoryService = itemCategoryService;
    }

    //  获取当前分类
    @GetMapping("/{cid}")
    @Operation(summary = "获取当前分类")
    public ApiResponse<ItemCategory> getCid(@PathVariable int cid) {
        return ResponseUtils.success(itemCategoryService.getById(cid));
    }

    //  一次性获取整个分类树
    @GetMapping
    @Operation(summary = "获取整个分类")
    public ApiResponse<List<ItemCategory>> getAll() {
        return ResponseUtils.success(itemCategoryService.list());
    }

    //  获取层级
    @GetMapping("/parent/{pid}")
    @Operation(summary = "获取父ID分类")
    public ApiResponse<List<ItemCategory>> getLevel(@PathVariable int pid) {
        return ResponseUtils.success(itemCategoryService.getParentcid(pid));
    }


}
