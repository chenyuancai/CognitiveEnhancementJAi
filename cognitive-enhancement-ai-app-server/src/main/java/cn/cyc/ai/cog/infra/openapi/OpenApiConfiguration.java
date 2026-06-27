package cn.cyc.ai.cog.infra.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Knife4j 文档配置。
 *
 * @author cyc
 */
@Configuration
public class OpenApiConfiguration {

    @Value("${cog.openapi.gateway-url:http://localhost:8801}")
    private String gatewayUrl;

    /**
     * 全局 OpenAPI 元信息与 JWT 鉴权声明。
     */
    @Bean
    public OpenAPI cognitiveEnhancementOpenApi() {
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("从 POST /api/auth/login 获取 Token，填入 Authorization: Bearer {token}");
        return new OpenAPI()
                .info(new Info()
                        .title("Cognitive Enhancement AI API")
                        .description("认知增强 AI 平台开放接口，覆盖 Center 元数据治理与 Runtime 运行时能力。")
                        .version("1.0.0")
                        .contact(new Contact().name("Cognitive Enhancement AI")))
                .servers(List.of(new Server().url(gatewayUrl).description("API 网关")))
                .components(new Components().addSecuritySchemes("BearerAuth", bearerAuth))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }

    /**
     * 全部 API 分组。
     */
    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all")
                .displayName("全部接口")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * Center 元数据管理分组。
     */
    @Bean
    public GroupedOpenApi centerApis() {
        return GroupedOpenApi.builder()
                .group("center")
                .displayName("Center 元数据")
                .pathsToMatch("/api/center/**")
                .build();
    }

    /**
     * Runtime 运行时能力分组。
     */
    @Bean
    public GroupedOpenApi runtimeApis() {
        return GroupedOpenApi.builder()
                .group("runtime")
                .displayName("Runtime 运行时")
                .pathsToMatch("/api/runtime/**")
                .build();
    }

    /**
     * 鉴权与用户分组。
     */
    @Bean
    public GroupedOpenApi authApis() {
        return GroupedOpenApi.builder()
                .group("auth")
                .displayName("鉴权与用户")
                .pathsToMatch("/api/auth/**", "/api/users/**")
                .build();
    }

    /**
     * 管理 Harness 分组。
     */
    @Bean
    public GroupedOpenApi adminApis() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("后台管理")
                .pathsToMatch("/api/admin/**")
                .build();
    }
}
