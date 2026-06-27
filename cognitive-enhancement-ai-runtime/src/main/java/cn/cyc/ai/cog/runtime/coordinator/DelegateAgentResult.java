package cn.cyc.ai.cog.runtime.coordinator;

import java.util.Map;

/**
 * 子 Agent 委派执行结果。
 *
 * @param agentCode 子 Agent 编码
 * @param status    执行状态
 * @param output    输出摘要
 * @author cyc
 */
public record DelegateAgentResult(String agentCode, String status, Map<String, Object> output) {
}
