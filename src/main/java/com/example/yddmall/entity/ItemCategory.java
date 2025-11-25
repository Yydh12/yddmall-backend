package com.example.yddmall.entity;

    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品类目表")
public class ItemCategory {

    @Schema(description = "类目ID")
        @TableId(value = "cid", type = IdType.AUTO)
    private Long cid;

    @Schema(description = "父类目ID，0表示根节点")
        @TableField("parent_cid")
    private Long parentCid;

    @Schema(description = "类目名称")
        @TableField("name")
    private String name;

    @Schema(description = "层级，1-一级，2-二级，3-三级")
        @TableField("level")
    private Byte level;

    @Schema(description = "是否叶子类目，1-是，0-否")
        @TableField("is_leaf")
    private Byte isLeaf;

}
