package cn.cyc.ai.cog.runtime.api;

import java.util.List;
import java.util.Map;

/**
 * 模型状态刷新请求。
 *
 * @param modelCode  单个模型编码
 * @param modelCodes 批量模型编码
 * @param prompt     检查提示词
 * @param parameters 执行参数
 * @author cyc
 */
public record ModelStatusRefreshRequest(String modelCode,
                                        List<String> modelCodes,
                                        String prompt,
                                        Map<String, Object> parameters) {
}
