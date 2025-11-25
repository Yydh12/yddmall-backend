package com.example.yddmall.service;

import com.example.yddmall.entity.Address;
import com.example.yddmall.utils.GeoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FreightService {

    @Value("${delivery.origin.lat:39.9042}")
    private double originLat; // 默认北京

    @Value("${delivery.origin.lng:116.4074}")
    private double originLng;

    /**
     * 简单分段运费策略：
     * 0-3km免费；3-10km 5；10-30km 10；>30km 20
     */
    public BigDecimal computeFreight(Address address) {
        if (address == null || address.getLat() == null || address.getLng() == null) {
            return BigDecimal.ZERO;
        }
        double km = GeoUtils.haversineKm(originLat, originLng, address.getLat(), address.getLng());
        if (km <= 3) return BigDecimal.ZERO;
        if (km <= 10) return BigDecimal.valueOf(5);
        if (km <= 30) return BigDecimal.valueOf(10);
        return BigDecimal.valueOf(20);
    }
}