package com.car.rental.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI3 接口文档全局配置
 *
 * 文档地址：http://localhost:8088/doc.html（免登录访问）
 * 调试方法：先调 /api/auth/login 拿 accessToken，在文档右上角「Authorize」中
 * 填入 "Bearer {accessToken}"（注意带 Bearer 前缀和空格），即可调试需登录的接口
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("豪车租赁管理系统 - 后台服务 API")
                        .description("""
                                后台管理服务全部接口文档。

                                ## 通用说明
                                - 除登录/注册/公开接口外，所有接口需在请求头携带 JWT：`Authorization: Bearer {accessToken}`
                                - 统一返回结构：`{ "code": 200, "msg": "success", "data": ... }`，code=200 成功，401 未登录，403 权限不足
                                - 按钮级权限由后端 @RequirePermission 注解校验，权限不足返回 403（提示信息不暴露具体权限标识）
                                - 分页接口统一参数：page（页码，从 1 开始）、pageSize（每页条数），返回结构含 list/total
                                """)
                        .version("1.0.0"))
                // 全局鉴权配置：文档页 Authorize 一次填写，调试所有需登录接口
                .components(new Components().addSecuritySchemes("Authorization",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("格式：Bearer {accessToken}（先调登录接口获取 accessToken）")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
}
