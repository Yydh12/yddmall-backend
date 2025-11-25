package ${package.Controller};

<#--<#list table.importPackages as pkg>-->
<#--    import ${pkg};-->
<#--</#list>-->
import com.example.yddmall.service.${table.serviceName};
import com.example.yddmall.entity.${table.entityName};
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if><#if controllerMappingHyphenStyle?? && controllerMappingHyphenStyle>${table.entityPath?replace("([A-Z])", "-$1", "r")?lower_case}<#else>${table.entityPath}</#if>")
@Tag(name = "${table.comment!}接口")
public class ${table.controllerName} {

    private final ${table.serviceName} ${table.serviceName?uncap_first};

    public ${table.controllerName}(${table.serviceName} ${table.serviceName?uncap_first}){
        this.${table.serviceName?uncap_first} = ${table.serviceName?uncap_first};
    }

    //分页查询
    @GetMapping
    public Page<${table.entityName}> page(Page<${table.entityName}> page, ${table.entityName} ${table.entityName?uncap_first}) {
        return ${table.serviceName?uncap_first}.page(page, new QueryWrapper<>(${table.entityName?uncap_first}));
    }

    //通过id查询单条数据
    @GetMapping("/{id}")
    public ${table.entityName} getById(@PathVariable Long id) {
        return ${table.serviceName?uncap_first}.getById(id);
    }

    //新增数据
    @PostMapping
    public boolean save(@RequestBody ${table.entityName} ${table.entityName?uncap_first}) {
        return ${table.serviceName?uncap_first}.save(${table.entityName?uncap_first});
    }

    //修改数据
    @PutMapping
    public boolean updateById(@RequestBody ${table.entityName} ${table.entityName?uncap_first}) {
        return ${table.serviceName?uncap_first}.updateById(${table.entityName?uncap_first});
    }

    //删除数据
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return ${table.serviceName?uncap_first}.removeById(id);
    }
}
