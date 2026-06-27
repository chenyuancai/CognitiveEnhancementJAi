package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * Tool 调用结果对象。
 *
 * @param executorType    执行器类型
 * @param toolCode        Tool 编码
 * @param protocolType    Tool 协议类型
 * @param permissionScope 权限范围
 * @param riskLevel       风险等级
 * @param input           工具输入
 * @param parameters      透传执行参数
 * @param toolPayload     工具实际载荷
 * @param mock            是否为 mock 返回
 * @author cyc
 */
public record ToolInvocationResult(String executorType,
                                   String toolCode,
                                   String protocolType,
                                   String permissionScope,
                                   String riskLevel,
                                   Object input,
                                   Map<String, Object> parameters,
                                   Object toolPayload,
                                   boolean mock) {
}
