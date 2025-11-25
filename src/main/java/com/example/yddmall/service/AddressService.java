package com.example.yddmall.service;


import com.example.yddmall.dto.AddressDTO;
import com.example.yddmall.entity.Address;

import java.util.List;

public interface AddressService {
    
    /**
     * 添加收货地址
     */
    Address addAddress(AddressDTO addressDTO, Long userId);
    
    /**
     * 更新收货地址
     */
    Address updateAddress(Long addressId, AddressDTO addressDTO, Long userId);
    
    /**
     * 删除收货地址
     */
    void deleteAddress(Long addressId, Long userId);
    
    /**
     * 获取用户的收货地址列表
     */
    List<Address> getUserAddresses(Long userId);
    
    /**
     * 获取用户的默认地址
     */
    Address getDefaultAddress(Long userId);
    
    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long addressId, Long userId);
    
    /**
     * 根据ID获取地址
     */
    Address getAddressById(Long addressId, Long userId);
}