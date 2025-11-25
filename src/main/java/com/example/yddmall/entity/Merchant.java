package com.example.yddmall.entity;

    import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
    import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商家表")
public class Merchant {

    @Schema(description = "商家ID（主键）")
        @TableId(value = "merchant_id", type = IdType.AUTO)
    private Long merchantId;

    @Schema(description = "商家编号（用于文件目录及外部展示）")
    private String merchantNo;

    @Schema(description = "店铺主图（访问URL）")
    private String merchantPic;

    @Schema(description = "商家名称")
    private String merchantName;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @TableField("province")
    private String province;
    
    @TableField("city")
    private String city;
    
    @TableField("district")
    private String district;

    @Schema(description = "商家地址")
    private String address;

    @Schema(description = "状态（0-禁用，1-正常，2-待审核）")
    private Byte status;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

}
