package cn.cyc.ai.cog.runtime.reflection;

import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;

/**
 * 自反思执行结果。
 *
 * @param result         最终 LLM 结果
 * @param applied        是否执行了反思重试
 * @param retryCount     反思重试次数
 * @param reflectionNote 反思说明
 * @author cyc
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
