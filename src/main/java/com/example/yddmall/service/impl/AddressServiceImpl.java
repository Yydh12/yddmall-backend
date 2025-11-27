package com.example.yddmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yddmall.dto.AddressDTO;
import com.example.yddmall.entity.Address;
import com.example.yddmall.mapper.AddressMapper;
import com.example.yddmall.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;
    
    @Override
    @Transactional
    public Address addAddress(AddressDTO addressDTO, Long userId) {
        if (addressDTO == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        String rn = addressDTO.getReceiverName();
        String rp = addressDTO.getReceiverPhone();
        String pv = addressDTO.getProvince();
        String ct = addressDTO.getCity();
        String dt = addressDTO.getDistrict();
        String da = addressDTO.getDetailAddress();
        if (rn == null || rn.trim().isEmpty()) {
            throw new IllegalArgumentException("请填写收货人姓名");
        }
        if (rp == null || rp.trim().isEmpty()) {
            throw new IllegalArgumentException("请填写收货人手机号");
        }
        if (pv == null || pv.trim().isEmpty()) {
            throw new IllegalArgumentException("请选择省份");
        }
        if (ct == null || ct.trim().isEmpty()) {
            throw new IllegalArgumentException("请选择城市");
        }
        if (dt == null || dt.trim().isEmpty()) {
            throw new IllegalArgumentException("请选择区县");
        }
        if (da == null || da.trim().isEmpty()) {
            throw new IllegalArgumentException("请输入详细地址");
        }
        // 如果这是第一个地址，设置为默认地址
        long count = this.count(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        address.setIsDefault(count == 0 ? 1 : (addressDTO.getIsDefault() == null ? 0 : addressDTO.getIsDefault()));
        
        this.save(address);
        return address;
    }
    
    @Override
    @Transactional
    public Address updateAddress(Long addressId, AddressDTO addressDTO, Long userId) {
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或无权限");
        }
        
        BeanUtils.copyProperties(addressDTO, address);
        this.updateById(address);
        return address;
    }
    
    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或无权限");
        }
        
        // 如果是默认地址，删除后需要重新设置默认地址
        if (address.getIsDefault() == 1) {
            this.removeById(addressId);
            // 重新设置默认地址
            List<Address> remainingAddresses = this.list(new LambdaQueryWrapper<Address>()
                    .eq(Address::getUserId, userId)
                    .orderByDesc(Address::getCreateTime));
            
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(1);
                this.updateById(newDefault);
            }
        } else {
            this.removeById(addressId);
        }
    }
    
    @Override
    public List<Address> getUserAddresses(Long userId) {
        return addressMapper.selectByUserId(userId);
    }
    
    @Override
    public Address getDefaultAddress(Long userId) {
        return addressMapper.selectDefaultByUserId(userId);
    }
    
    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        // 取消所有默认地址
        addressMapper.cancelDefaultByUserId(userId);
        
        // 设置新的默认地址
        Address address = this.getById(addressId);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(1);
            this.updateById(address);
        }
    }
    
    @Override
    public Address getAddressById(Long addressId, Long userId) {
        Address address = this.getById(addressId);
        if (address != null && address.getUserId().equals(userId)) {
            return address;
        }
        return null;
    }
}
