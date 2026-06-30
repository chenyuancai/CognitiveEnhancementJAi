package cn.cyc.ai.cog.sse.api;

/**
 * SSE 服务常量。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class SseConstants {

    /** 服务名称。 */
    public static final String SERVICE_NAME = "cognitive-enhancement-ai-sse-server";

    /** 前端经网关访问的连接 API 前缀。 */
    public static final String CLIENT_API_PREFIX = "/api/sse";

    /**
     * 服务间 Feign 推送 API 前缀（与 {@link #CLIENT_API_PREFIX} 相同，走网关 {@code /api/sse/**}）。
     * <p>推送路径：{@code POST /api/sse/send}</p>
     */
    public static final String INNER_API_PREFIX = CLIENT_API_PREFIX;

    /**
     * 创建Sse常量定义。
     */
    private SseConstants() {
    }
}
