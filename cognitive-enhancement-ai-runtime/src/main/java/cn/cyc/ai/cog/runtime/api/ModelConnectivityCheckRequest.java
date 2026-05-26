package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * 模型连通性检查请求。
 *
 * @param modelCode   模型编码
 * @param prompt      检查提示词
 * @param parameters  执行参数
 * @author cyc
 */
public record ModelConnectivityCheckRequest(String modelCode,
                                            String prompt,
                                            Map<String, Object> parameters) {
}
