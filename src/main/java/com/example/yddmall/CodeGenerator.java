package com.example.yddmall;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;
import java.util.Scanner;

public class CodeGenerator {
    // 数据库连接配置
    private static final String URL = "jdbc:mysql://localhost:3306/yddmall?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    // 父包名
    private static final String PARENT_PACKAGE = "com.example.yddmall";

    // 作者
    private static final String AUTHOR = "Yy";

    public static void main(String[] args) {
        // 交互式输入表名
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要生成的表名(多个表用逗号分隔): ");
        String tables = scanner.nextLine();
        scanner.close();

        // 项目根路径
        String projectPath = System.getProperty("user.dir");

        // 生成代码的输出路径
        String outputDir = projectPath + "/src/main/java";

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                // 全局配置
                .globalConfig(builder -> builder
                        .author(AUTHOR)                    // 作者名
                        .outputDir(outputDir)              // 输出路径
                        .dateType(DateType.ONLY_DATE)      // 日期类型
                        .commentDate("yyyy-MM-dd")         // 注释日期格式
                        .disableOpenDir()                  // 禁止自动打开输出目录
                        .enableSwagger()                   // 开启Swagger注解支持
                )

                // 包配置
                .packageConfig(builder -> builder
                        .parent(PARENT_PACKAGE)            // 父包名
                        .entity("entity")                  // 实体类包名
                        .mapper("mapper")                  // Mapper接口包名
                        .service("service")                // Service接口包名
                        .serviceImpl("service.impl")       // Service实现类包名
                        .controller("controller")          // Controller包名
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                projectPath + "/src/main/resources/mapper"  // MapperXML路径
                        ))
                )

                // 策略配置
                .strategyConfig(builder -> {
                    // 全局策略
                    builder.addInclude(tables.split(","))     // 要生成的表名
                            .addTablePrefix("t_", "sys_");   // 表前缀过滤

                    // 实体类策略
                    builder.entityBuilder()
                            .enableLombok()                    // 启用Lombok
                            .enableTableFieldAnnotation()      // 启用字段注解
                            .logicDeleteColumnName("deleted")  // 逻辑删除字段
                            .addTableFills(                    // 自动填充配置
                                    new Column("create_time", FieldFill.INSERT),
                                    new Column("update_time", FieldFill.INSERT_UPDATE)
                            )
                            .versionColumnName("version");     // 乐观锁字段 - 替代enableVersion()

                    // Controller策略
                    builder.controllerBuilder()
                            .enableRestStyle()                 // 启用RestController
                            .enableHyphenStyle();              // 启用连字符命名风格

                    // Service策略
                    builder.serviceBuilder()
                            .formatServiceFileName("%sService") // 去掉Service接口的I前缀
                            .formatServiceImplFileName("%sServiceImpl");

                    // Mapper策略
                    builder.mapperBuilder()
                            .enableBaseResultMap()             // 启用BaseResultMap
                            .enableBaseColumnList();           // 启用BaseColumnList
                })

                // 使用自定义模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
