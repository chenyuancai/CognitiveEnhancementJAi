package cn.cyc.ai.cog.sse.config;

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
 * SSE-Server OpenAPI 配置。
 */
@Configuration
public class SseOpenApiConfiguration {

    @Value("${cog.openapi.gateway-url:http://localhost:8801}")
    private String gatewayUrl;

    @Bean
    public OpenAPI sseOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cognitive Enhancement AI SSE API")
                        .description("SSE 推送服务：客户端长连接与服务间事件推送")
                        .version("1.0.0")
                        .contact(new Contact().name("Cognitive Enhancement AI")))
                .servers(List.of(new Server().url(gatewayUrl).description("API 网关")));
    }

    @Bean
    public GroupedOpenApi sseApis() {
        return GroupedOpenApi.builder()
                .group("sse")
                .displayName("SSE 推送")
                .pathsToMatch("/api/sse/**")
                .build();
    }
}
