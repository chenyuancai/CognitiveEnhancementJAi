package cn.cyc.ai.cog.runtime.coordinator;

import java.util.Map;

/**
 * 子 Agent 委派执行结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record DelegateAgentResult(String agentCode, String status, Map<String, Object> output) {
}
