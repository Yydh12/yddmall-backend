package com.example.yddmall.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class AddressDTO implements Serializable {
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String detailAddress;
    
    private String postalCode;

    // 维度：地理位置纬度
    private Double lat;

    // 经度：地理位置经度
    private Double lng;

    private Integer isDefault = 0;
}