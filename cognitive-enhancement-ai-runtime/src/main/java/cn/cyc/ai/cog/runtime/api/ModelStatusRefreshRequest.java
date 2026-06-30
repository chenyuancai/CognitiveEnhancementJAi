package cn.cyc.ai.cog.runtime.api;

import java.util.List;
import java.util.Map;

/**
 * 模型状态刷新请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ModelStatusRefreshRequest(String modelCode,
                                        List<String> modelCodes,
                                        String prompt,
                                        Map<String, Object> parameters) {
}
