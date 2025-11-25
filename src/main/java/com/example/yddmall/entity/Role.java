package com.example.yddmall.entity;

    import com.baomidou.mybatisplus.annotation.FieldFill;
    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.baomidou.mybatisplus.annotation.TableId;
    import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色表")
public class Role {

    @Schema(description = "角色ID（主键）")
        @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @Schema(description = "角色名称")
        @TableField("role_name")
    private String roleName;

    @Schema(description = "角色标识（如admin、merchant_admin）")
        @TableField("role_key")
    private String roleKey;

    @Schema(description = "排序（值越小越靠前）")
        @TableField("sort")
    private Integer sort;

    @Schema(description = "是否全局角色（0-商家角色，1-平台全局角色）")
        @TableField("is_global")
    private Byte isGlobal;

    @Schema(description = "状态（0-禁用，1-正常）")
        @TableField("status")
    private Byte status;

    @Schema(description = "创建时间")
        @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "更新时间")
        @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "备注")
        @TableField("remark")
    private String remark;

}
