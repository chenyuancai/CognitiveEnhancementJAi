package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.runtime.api.LlmHttpRequest;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;
import cn.cyc.ai.cog.runtime.api.LlmInvocationRequest;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.spi.LlmCredentialResolver;
import cn.cyc.ai.cog.runtime.spi.LlmHttpExecutor;
import cn.cyc.ai.cog.runtime.spi.LlmProviderHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 百炼 OpenAI 兼容模式 Provider 处理器。
 *
 * @author cyc
 */
@Component
public class BailianLlmProviderHandler implements LlmProviderHandler {

    /**
     * 处理器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(BailianLlmProviderHandler.class);

    /**
     * 百炼 provider 编码。
     */
    private static final String PROVIDER_CODE = "bailian";

    /**
     * Chat completions 路径。
     */
    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    /**
     * JSON 处理器。
     */
    private final ObjectMapper objectMapper;

    /**
     * 凭证解析器。
     */
    private final LlmCredentialResolver llmCredentialResolver;

    /**
     * HTTP 执行器。
     */
    private final LlmHttpExecutor llmHttpExecutor;

    /**
     * 构造百炼处理器。
     *
     * @param objectMapper          JSON 处理器
     * @param llmCredentialResolver 凭证解析器
     * @param llmHttpExecutor       HTTP 执行器
     */
    public BailianLlmProviderHandler(ObjectMapper objectMapper,
                                     LlmCredentialResolver llmCredentialResolver,
                                     LlmHttpExecutor llmHttpExecutor) {
        this.objectMapper = objectMapper;
        this.llmCredentialResolver = llmCredentialResolver;
        this.llmHttpExecutor = llmHttpExecutor;
    }

    /**
     * 判断是否支持百炼 provider。
     *
     * @param providerCode 模型提供方编码
     * @return 是否支持
     */
    @Override
    public boolean supports(String providerCode) {
        return PROVIDER_CODE.equals(providerCode);
    }

    /**
     * 执行一次百炼调用。
     *
     * @param request LLM 调用请求
     * @return LLM 调用结果
     */
    @Override
    public LlmInvocationResult generate(LlmInvocationRequest request) {
        String endpoint = resolveEndpoint(request.endpoint());
        String apiKey = llmCredentialResolver.resolve(request.credentialRef());
        String requestBody = buildRequestBody(request);
        LlmHttpResponse response = llmHttpExecutor.execute(new LlmHttpRequest(
                endpoint,
                Map.of("Authorization", "Bearer " + apiKey),
                requestBody,
                Duration.ofMillis(request.timeoutMs())
        ));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new BusinessException("CONFLICT", "百炼模型调用失败，HTTP 状态码: " + response.statusCode());
        }
        String answer = parseAnswer(response.body());
        log.info("执行百炼模型调用完成, traceId={}, capabilityCode={}, agentCode={}, modelCode={}, endpoint={}",
                request.traceId(), request.capabilityCode(), request.agentCode(), request.modelCode(), endpoint);
        return new LlmInvocationResult(
                "LLM",
                request.providerCode(),
                request.modelCode(),
                request.promptCode(),
                request.promptInput(),
                answer,
                request.parameters(),
                false
        );
    }

    /**
     * 解析最终请求地址。
     *
     * @param endpoint 原始 endpoint
     * @return 完整请求地址
     */
    private String resolveEndpoint(String endpoint) {
        if (!StringUtils.hasText(endpoint)) {
            throw new BusinessException("INVALID_ARGUMENT", "百炼 endpoint 不能为空");
        }
        if (endpoint.endsWith(CHAT_COMPLETIONS_PATH)) {
            return endpoint;
        }
        if (endpoint.endsWith("/")) {
            return endpoint.substring(0, endpoint.length() - 1) + CHAT_COMPLETIONS_PATH;
        }
        return endpoint + CHAT_COMPLETIONS_PATH;
    }

    /**
     * 构造百炼请求体。
     *
     * @param request LLM 调用请求
     * @return JSON 请求体
     */
    private String buildRequestBody(LlmInvocationRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", request.modelCode());
        payload.put("messages", List.of(Map.of(
                "role", "user",
                "content", String.valueOf(request.promptInput())
        )));
        appendParameter(payload, request.parameters(), "temperature", "temperature");
        appendParameter(payload, request.parameters(), "topP", "top_p");
        appendParameter(payload, request.parameters(), "maxTokens", "max_tokens");
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("CONFLICT", "百炼请求体序列化失败: " + exception.getMessage(), exception);
        }
    }

    /**
     * 附加可选参数。
     *
     * @param payload       请求体
     * @param parameters    执行参数
     * @param sourceKey     源参数名
     * @param payloadKey    请求体参数名
     */
    private void appendParameter(Map<String, Object> payload,
                                 Map<String, Object> parameters,
                                 String sourceKey,
                                 String payloadKey) {
        if (parameters == null || !parameters.containsKey(sourceKey)) {
            return;
        }
        payload.put(payloadKey, parameters.get(sourceKey));
    }

    /**
     * 解析百炼响应中的回答文本。
     *
     * @param responseBody 响应体
     * @return 回答文本
     */
    private String parseAnswer(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode answerNode = rootNode.path("choices").path(0).path("message").path("content");
            if (!answerNode.isTextual()) {
                throw new BusinessException("CONFLICT", "百炼响应中缺少 message.content");
            }
            return answerNode.asText();
        } catch (JsonProcessingException exception) {
            throw new BusinessException("CONFLICT", "百炼响应解析失败: " + exception.getMessage(), exception);
        }
    }
}
