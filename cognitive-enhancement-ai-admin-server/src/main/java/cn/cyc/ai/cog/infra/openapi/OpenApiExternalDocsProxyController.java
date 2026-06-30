package cn.cyc.ai.cog.infra.openapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 将独立进程的 OpenAPI 分组代理到 admin，避免 doc.html 请求 {@code /v3/api-docs/sse} 时 500。
 */
@RestController
@ConditionalOnProperty(name = "cog.openapi.external-docs-proxy.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiExternalDocsProxyController {

    private static final Logger log = LoggerFactory.getLogger(OpenApiExternalDocsProxyController.class);

    private final RestClient restClient;

    @Value("${cog.openapi.base-service-url:http://localhost:8805}")
    private String baseServiceUrl;

    @Value("${cog.openapi.sse-service-url:http://localhost:8806}")
    private String sseServiceUrl;

    public OpenApiExternalDocsProxyController(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @GetMapping(value = "/v3/api-docs/base", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> proxyBaseDocs() {
        return proxy(baseServiceUrl + "/v3/api-docs/base", "base");
    }

    @GetMapping(value = "/v3/api-docs/sse", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> proxySseDocs() {
        return proxy(sseServiceUrl + "/v3/api-docs/sse", "sse");
    }

    private ResponseEntity<String> proxy(String targetUrl, String group) {
        try {
            String body = restClient.get()
                    .uri(targetUrl)
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        } catch (RestClientException exception) {
            log.warn("代理 OpenAPI 分组失败: group={}, target={}, message={}",
                    group, targetUrl, exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"无法拉取 " + group + " OpenAPI，请确认对应服务已启动\"}");
        }
    }
}
