package ${package.Entity};

<#list table.importPackages as pkg>
    import ${pkg};
</#list>
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

<#--/**-->
<#--* ${table.comment!}-->
<#--*-->
<#--* @author ${author}-->
<#--* @since ${date}-->
<#--*/-->
@Data
@Schema(description = "${table.comment!}")
<#if superEntityClass??>
public class ${table.entityName} extends ${superEntityClass} {
<#elseif activeRecord>
public class ${table.entityName} extends Model<${table.entityName}> {
<#else>
public class ${table.entityName} {
</#if>
<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName = field.propertyName>
    <#-- 确保idType有默认值 -->
        <#if field.idType??>
            <#assign idType = field.idType>
        <#else>
            <#assign idType = "AUTO">
        </#if>
    </#if>

<#-- 字段注释和验证注解 -->
    @Schema(description = "${field.comment!}")
<#--    <#if field.fill??>-->
<#--        @TableField(value = "${field.name}", fill = FieldFill.${field.fill})-->
<#--    <#else>-->
<#--        @TableField("${field.name}")-->
<#--    </#if>-->
    <#if field.keyFlag>
        @TableId(value = "${field.name}", type = IdType.${idType})
    </#if>
<#-- 处理versionFlag可能为null的情况 -->
    <#if field.versionFlag?? && field.versionFlag>
        @Version
    </#if>
<#-- 处理logicDeleteFlag可能为null的情况 -->
    <#if field.logicDeleteFlag?? && field.logicDeleteFlag>
        @TableLogic
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>

<#if activeRecord>
    @Override
    protected Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }
</#if>
}
