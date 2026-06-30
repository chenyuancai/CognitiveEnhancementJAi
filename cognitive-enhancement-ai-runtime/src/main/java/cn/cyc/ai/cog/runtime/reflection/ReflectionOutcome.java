package cn.cyc.ai.cog.runtime.reflection;

import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;

/**
 * 自反思执行结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ReflectionOutcome(LlmInvocationResult result,
                                boolean applied,
                                int retryCount,
                                String reflectionNote) {

    /**
     * 未触发反思时的结果包装。
     *
     * @param result 原始 LLM 结果
     * @return 反思结果
     */
    public static ReflectionOutcome unchanged(LlmInvocationResult result) {
        return new ReflectionOutcome(result, false, 0, null);
    }
}
