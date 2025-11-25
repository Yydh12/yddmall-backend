package com.example.yddmall.controller;

import com.example.yddmall.service.RoleService;
import com.example.yddmall.entity.Role;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/role")
@Tag(name = "角色表接口")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    //分页查询
    @GetMapping
    public Page<Role> page(Page<Role> page, Role role) {
        return roleService.page(page, new QueryWrapper<>(role));
    }

    //通过id查询单条数据
    @GetMapping("/{id}")
    public Role getById(@PathVariable Long id) {
        return roleService.getById(id);
    }

    //新增数据
    @PostMapping
    public boolean save(@RequestBody Role role) {
        return roleService.save(role);
    }

    //修改数据
    @PutMapping
    public boolean updateById(@RequestBody Role role) {
        return roleService.updateById(role);
    }

    //删除数据
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return roleService.removeById(id);
    }
}
