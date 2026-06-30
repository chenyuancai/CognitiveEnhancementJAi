package cn.cyc.ai.cog.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cognitive Enhancement JAi Java SDK 客户端。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class CogSdkClient {

    /** EXECUTE路径。 */
    private static final String EXECUTE_PATH = "/api/runtime/capabilities/execute";
    /** SDKTRANSPORT错误编码。 */
    private static final String SDK_TRANSPORT_ERROR_CODE = "SDK_TRANSPORT_ERROR";
    /** SDK响应错误编码。 */
    private static final String SDK_RESPONSE_ERROR_CODE = "SDK_RESPONSE_ERROR";

    /** 配置。 */
    private final CogSdkClientConfig config;
    /** transport。 */
    private final CogSdkTransport transport;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /**
     * 创建CogSdk客户端。
     *
     * @param config 配置
     * @param transport transport
     * @param objectMapper JSON 序列化器
     */
    private CogSdkClient(CogSdkClientConfig config, CogSdkTransport transport, ObjectMapper objectMapper) {
        this.config = config;
        this.transport = transport;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建Item。
     *
     * @param config 配置
     * @return 创建结果
     */
    public static CogSdkClient create(CogSdkClientConfig config) {
        return new CogSdkClient(config, new JavaNetCogSdkTransport(config), new ObjectMapper());
    }

    static CogSdkClient create(CogSdkClientConfig config, CogSdkTransport transport, ObjectMapper objectMapper) {
        return new CogSdkClient(config, transport, objectMapper);
    }

    /**
     * 同步执行能力。
     *
     * @param request 能力执行请求
     * @return 执行结果摘要
     */
    public CapabilityExecutionResult executeCapability(CapabilityExecutionRequest request) {
        String responseBody = sendExecuteRequest(request);
        JsonNode root = parseJson(responseBody, 200);
        if (!root.path("success").asBoolean(false)) {
            throw apiException(200, root);
        }
        JsonNode data = root.path("data");
        JsonNode result = data.path("result");
        return new CapabilityExecutionResult(
                data.path("traceId").asText(root.path("traceId").asText(null)),
                result.path("status").asText(null),
                result.path("message").asText(null),
                toMap(result.path("output"))
        );
    }

    /**
     * 执行sendExecute请求。
     *
     * @param request 请求
     * @return 执行结果
     */
    private String sendExecuteRequest(CapabilityExecutionRequest request) {
        SdkHttpRequest httpRequest = new SdkHttpRequest(
                "POST",
                config.baseUrl() + EXECUTE_PATH,
                headers(),
                toJson(request),
                config.timeout()
        );
        try {
            SdkHttpResponse response = transport.send(httpRequest);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw apiException(response.statusCode(), parseJson(response.body(), response.statusCode()));
            }
            return response.body();
        } catch (IOException e) {
            throw new CogSdkException(0, SDK_TRANSPORT_ERROR_CODE, "SDK HTTP 调用失败: " + e.getMessage(), null, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CogSdkException(0, SDK_TRANSPORT_ERROR_CODE, "SDK HTTP 调用被中断", null, e);
        }
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        if (hasText(config.bearerToken())) {
            headers.put("Authorization", "Bearer " + config.bearerToken());
        }
        if (hasText(config.tenantCode())) {
            headers.put("X-Tenant-Code", config.tenantCode());
        }
        if (hasText(config.traceId())) {
            headers.put("X-Trace-Id", config.traceId());
        }
        return headers;
    }

    /**
     * 转换为JSON。
     *
     * @param value 值
     * @return 转换结果
     */
    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new CogSdkException(0, SDK_RESPONSE_ERROR_CODE, "SDK 请求序列化失败: " + e.getMessage(), null, e);
        }
    }

    /**
     * 执行parseJSON。
     *
     * @param body body
     * @param httpStatus http状态
     * @return 执行结果
     */
    private JsonNode parseJson(String body, int httpStatus) {
        try {
            return objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new CogSdkException(httpStatus, SDK_RESPONSE_ERROR_CODE,
                    "SDK 响应解析失败: " + e.getMessage(), null, e);
        }
    }

    private Map<String, Object> toMap(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return Map.of();
        }
        return objectMapper.convertValue(node, new TypeReference<>() {
        });
    }

    /**
     * 执行apiException。
     *
     * @param httpStatus http状态
     * @param root root
     * @return 执行结果
     */
    private CogSdkException apiException(int httpStatus, JsonNode root) {
        return new CogSdkException(
                httpStatus,
                root.path("code").asText(SDK_RESPONSE_ERROR_CODE),
                root.path("message").asText("SDK 调用失败"),
                root.path("traceId").asText(null)
        );
    }

    /**
     * 判断是否包含Text。
     *
     * @param value 值
     * @return 是否包含
     */
    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
