package com.example.yddmall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "店铺统计信息")
public class MerchantStatsVO {

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "粉丝数（收藏商家人数）")
    private Long fansCount;

    @Schema(description = "在售商品数")
    private Long onSaleCount;

    @Schema(description = "平均评分（1-5）")
    private Double avgRating;

    @Schema(description = "好评率（0-1）")
    private Double positiveRate;
}