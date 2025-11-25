package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.dto.AddressDTO;
import com.example.yddmall.entity.Address;
import com.example.yddmall.service.AddressService;
import com.example.yddmall.utils.SessionUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 添加收货地址
     */
    @PostMapping("/add")
    public ApiResponse<Address> addAddress(@RequestBody AddressDTO addressDTO, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            System.out.println("添加地址 - 用户ID: " + userId);
            
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            Address address = addressService.addAddress(addressDTO, userId);
            return new ApiResponse<>(200, "添加成功", address);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 更新收货地址
     */
    @PutMapping("/update/{addressId}")
    public ApiResponse<Address> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            System.out.println("更新地址 - 用户ID: " + userId);
            
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            Address address = addressService.updateAddress(addressId, addressDTO, userId);
            return new ApiResponse<>(200, "更新成功", address);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 删除收货地址
     */
    @DeleteMapping("/delete/{addressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long addressId, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            System.out.println("删除地址 - 用户ID: " + userId);
            
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            addressService.deleteAddress(addressId, userId);
            return new ApiResponse<>(200, "删除成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 获取用户所有收货地址
     */
    @GetMapping("/list")
    public ApiResponse<List<Address>> getUserAddresses(HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            System.out.println("获取地址列表 - 用户ID: " + userId);
            
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            List<Address> addresses = addressService.getUserAddresses(userId);
            System.out.println("获取地址列表成功 - 地址数量: " + (addresses != null ? addresses.size() : 0));
            return new ApiResponse<>(200, "获取成功", addresses);
        } catch (Exception e) {
            System.err.println("获取地址列表失败: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 获取默认收货地址
     */
    @GetMapping("/default")
    public ApiResponse<Address> getDefaultAddress(HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            System.out.println("获取默认地址 - 用户ID: " + userId);
            
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            Address address = addressService.getDefaultAddress(userId);
            return new ApiResponse<>(200, "获取成功", address);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 设置默认收货地址
     */
    @PostMapping("/setDefault/{addressId}")
    public ApiResponse<Void> setDefaultAddress(@PathVariable Long addressId, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            System.out.println("设置默认地址 - 用户ID: " + userId);
            
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            addressService.setDefaultAddress(addressId, userId);
            return new ApiResponse<>(200, "设置成功", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 根据ID获取收货地址
     */
    @GetMapping("/{addressId}")
    public ApiResponse<Address> getAddressById(@PathVariable Long addressId, HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            System.out.println("根据ID获取地址 - 用户ID: " + userId);
            
            if (userId == null) {
                return new ApiResponse<>(401, "身份校验失败，请重新登录", null);
            }
            
            Address address = addressService.getAddressById(addressId, userId);
            if (address == null) {
                return new ApiResponse<>(404, "地址不存在", null);
            }
            return new ApiResponse<>(200, "获取成功", address);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
}