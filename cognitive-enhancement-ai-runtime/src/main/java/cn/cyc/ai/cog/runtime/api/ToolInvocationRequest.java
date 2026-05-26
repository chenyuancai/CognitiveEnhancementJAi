package cn.cyc.ai.cog.runtime.api;

import java.util.Map;

/**
 * Tool 调用请求对象。
 *
 * @param traceId        链路标识
 * @param capabilityCode 能力编码
 * @param agentCode      Agent 编码
 * @param toolCode       Tool 编码
 * @param protocolType   Tool 协议类型
 * @param input          工具输入
 * @param parameters     透传执行参数
 * @author cyc
 */
public record ToolInvocationRequest(String traceId,
                                    String capabilityCode,
                                    String agentCode,
                                    String toolCode,
                                    String protocolType,
                                    Object input,
                                    Map<String, Object> parameters) {
}
