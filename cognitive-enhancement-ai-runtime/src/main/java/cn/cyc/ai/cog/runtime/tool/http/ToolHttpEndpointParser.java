package cn.cyc.ai.cog.runtime.tool.http;

import cn.cyc.ai.cog.core.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 解析 Tool implRef 中的 HTTP 端点配置。
 *
 * @author cyc
 */
public final class ToolHttpEndpointParser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ToolHttpEndpointParser() {
    }

    /**
     * HTTP 端点配置。
     *
     * @param url    目标 URL
     * @param method HTTP 方法
     */
    public record HttpEndpoint(String url, String method) {
    }

    /**
     * 解析 implRef。
     * <p>
     * 支持两种格式：
     * <ul>
     *   <li>纯 URL：{@code https://example.com/path}，默认 POST</li>
     *   <li>JSON：{@code {"url":"...","method":"POST"}}</li>
     * </ul>
     *
     * @param implRef 实现引用
     * @return HTTP 端点
     */
    public static HttpEndpoint parse(String implRef) {
        if (implRef == null || implRef.isBlank()) {
            throw new BusinessException("INVALID_ARGUMENT", "HTTP Tool implRef 不能为空");
        }
        String trimmed = implRef.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return new HttpEndpoint(trimmed, "POST");
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(trimmed);
            String url = node.path("url").asText(null);
            if (url == null || url.isBlank()) {
                throw new BusinessException("INVALID_ARGUMENT", "HTTP Tool implRef 缺少 url");
            }
            String method = node.path("method").asText("POST");
            return new HttpEndpoint(url, method.toUpperCase());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("INVALID_ARGUMENT", "HTTP Tool implRef 格式无效: " + implRef);
        }
    }
}
