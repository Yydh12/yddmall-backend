package com.example.yddmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("address")
public class Address {
    
    @TableId(value = "address_id", type = IdType.AUTO)
    private Long addressId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("receiver_name")
    private String receiverName;
    
    @TableField("receiver_phone")
    private String receiverPhone;
    
    @TableField("province")
    private String province;
    
    @TableField("city")
    private String city;
    
    @TableField("district")
    private String district;
    
    @TableField("detail_address")
    private String detailAddress;
    
    @TableField("postal_code")
    private String postalCode;

    // 维度：地理位置纬度
    @TableField("lat")
    private Double lat;

    // 经度：地理位置经度
    @TableField("lng")
    private Double lng;

    @TableField("is_default")
    private Integer isDefault;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}