package com.example.yddmall.entity;
import com.baomidou.mybatisplus.annotation.TableField;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@Schema(description = "商城权限用户表")
@TableName("\"user\"")
public class User implements Serializable {

    @Schema(description = "用户ID（主键，自增）")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @Schema(description = "用户编号（用于文件目录及外部展示）")
    @TableField("user_no")
    private String userNo;

//    @NotNull(message = "用户账号不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须为3-20个字符")
    @Schema(description = "登录用户名")
    @TableField("username")
    private String username;

    @NotNull(message = "用户密码不能为空")
    @Size(min = 6, max = 11, message = "密码长度必须是6-11个字符")
    @Schema(description = "加密密码")
    @TableField("password")
    private String password;

    @Schema(description = "加密盐值")
    @TableField("salt")
    private String salt;

    @Schema(description = "真实姓名")
    @TableField("real_name")
    private String realName;

    @Pattern(regexp = "^$|1[3-9]\\d{9}$", message = "手机号格式不正确（11位数字）")
    @Builder.Default
    @Schema(description = "手机号（可为空，空值会自动转为null）")
    @TableField("phone")
    private String phone = null;

    @Pattern(regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "邮箱格式不正确")
    @Builder.Default
    @Schema(description = "邮箱（可为空，空值会自动转为null）")
    @TableField("email")
    private String email = null;

    @Min(value = 0, message = "性别必须为0-2（0-未知，1-男，2-女）")
    @Max(value = 2, message = "性别必须为0-2（0-未知，1-男，2-女）")
    @Schema(description = "性别（0-未知，1-男，2-女）")
    @TableField("gender")
    private Byte gender;

    @Schema(description = "头像URL")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "所属商家ID（关联merchant表）")
    @TableField("merchant_id")
    private Long merchantId;

    @Schema(description = "所属角色ID")
    @TableField("role_id")
    private Long roleId;

    @Min(value = 0, message = "状态必须为0-3（0-禁用，1-正常，2-待审核，3-已锁定）")
    @Max(value = 3, message = "状态必须为0-3（0-禁用，1-正常，2-待审核，3-已锁定）")
    @Schema(description = "状态（0-禁用，1-正常，2-待审核，3-已锁定）")
    @TableField("status")
    private Byte status;

    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private Date lastLoginTime;

    @Schema(description = "最后登录IP")
    @TableField("last_login_ip")
    private String lastLoginIp;

    @Schema(description = "创建时间（自动填充）")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @Schema(description = "创建人ID")
    @TableField("create_by")
    private Long createBy;

    @Schema(description = "更新时间（自动填充）")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "更新人ID")
    @TableField("update_by")
    private Long updateBy;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;
}
