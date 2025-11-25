package com.example.yddmall.entity;

    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableId;
    import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "类目与属性的关联表")
public class CategoryProp {

    @Schema(description = "关联记录唯一标识")
        @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联类目表的cid")
    private Long cid;

    @Schema(description = "关联属性名表的pid")
    private Long pid;

    @Schema(description = "是否必选：1=必选，0=可选")
    private Byte isMandatory;

    @Schema(description = "显示顺序（数字越小越靠前）")
    private Integer sortOrder;

    @Schema(description = "属性类型：1=销售属性，2=非销售属性")
    private Byte propType;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

}
