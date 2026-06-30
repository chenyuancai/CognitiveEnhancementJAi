package cn.cyc.ai.cog.runtime.tool.adapter;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapterContext;
import cn.cyc.ai.cog.runtime.tool.http.ToolHttpEndpointParser;
import cn.cyc.ai.cog.runtime.tool.spi.ToolHttpExecutor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP Tool Adapter。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class HttpToolAdapter implements ToolAdapter {

    /** 工具HttpExecutor。 */
    private final ToolHttpExecutor toolHttpExecutor;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /**
     * 创建HttpToolAdapter。
     *
     * @param toolHttpExecutor 工具HttpExecutor
     * @param objectMapper JSON 序列化器
     */
    public HttpToolAdapter(ToolHttpExecutor toolHttpExecutor, ObjectMapper objectMapper) {
        this.toolHttpExecutor = toolHttpExecutor;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行protocol类型。
     * @return 执行结果
     */
    @Override
    public ToolProtocolType protocolType() {
        return ToolProtocolType.HTTP;
    }

    /**
     * 执行mock。
     *
     * @param tool 工具
     * @return 执行结果
     */
    @Override
    public boolean mock(ToolDefinition tool) {
        return false;
    }

    /**
     * 执行操作。
     *
     * @param context 上下文
     * @return 执行结果
     */
    @Override
    public Object invoke(ToolAdapterContext context) {
        ToolDefinition tool = context.tool();
        Object input = context.request().input();
        ToolHttpEndpointParser.HttpEndpoint endpoint = ToolHttpEndpointParser.parse(tool.implRef());
        String body;
        try {
            body = objectMapper.writeValueAsString(input instanceof Map<?, ?> map ? map : Map.of("input", input));
        } catch (Exception ex) {
            throw new BusinessException("INVALID_ARGUMENT", "Tool 输入序列化失败: " + ex.getMessage(), ex);
        }
        ToolHttpResponse response = toolHttpExecutor.execute(new ToolHttpRequest(
                endpoint.url(),
                endpoint.method(),
                Map.of("Content-Type", "application/json"),
                body,
                Duration.ofMillis(tool.timeoutMs())
        ));
        if (response.statusCode() >= 400) {
            throw new BusinessException("CONFLICT",
                    "HTTP Tool 调用失败, status=" + response.statusCode() + ", body=" + abbreviate(response.body()));
        }
        Map<String, Object> payload = parseResponseBody(response.body());
        payload.put("httpStatus", response.statusCode());
        payload.put("latencyMs", response.latencyMs());
        payload.put("endpoint", endpoint.url());
        return payload;
    }

    private Map<String, Object> parseResponseBody(String body) {
        if (body == null || body.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(body, new TypeReference<>() {
            });
        } catch (Exception ex) {
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("rawBody", body);
            return fallback;
        }
    }

    /**
     * 执行abbreviate。
     *
     * @param body body
     * @return 执行结果
     */
    private String abbreviate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() <= 200 ? body : body.substring(0, 200) + "...";
    }
}
