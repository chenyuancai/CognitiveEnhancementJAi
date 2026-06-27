package cn.cyc.ai.cog.base.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Base-Server OpenAPI 配置。
 */
@Configuration
public class BaseOpenApiConfiguration {

    @Value("${cog.openapi.gateway-url:http://localhost:8801}")
    private String gatewayUrl;

    @Bean
    public OpenAPI baseOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cognitive Enhancement AI Base API")
                        .description("基础服务：字典、枚举与文件存储")
                        .version("1.0.0")
                        .contact(new Contact().name("Cognitive Enhancement AI")))
                .servers(List.of(new Server().url(gatewayUrl).description("API 网关")));
    }

    @Bean
    public GroupedOpenApi baseApis() {
        return GroupedOpenApi.builder()
                .group("base")
                .displayName("基础服务")
                .pathsToMatch("/api/base/**")
                .build();
    }
}
