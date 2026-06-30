package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * 模型连通性检查请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelConnectivityCheckRequest(String modelCode,
                                            String prompt,
                                            Map<String, Object> parameters) {
}
