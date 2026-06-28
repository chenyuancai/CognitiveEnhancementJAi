package cn.cyc.ai.cog.runtime.coordinator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * 多 Agent 执行策略测试。
 */
class ExecutionStrategyTest {

    @Test
    void shouldParseKnownStrategy() {
        assertSame(ExecutionStrategy.QUALITY_FIRST, ExecutionStrategy.from("quality_first"));
    }

    @Test
    void shouldFallbackToBalancedForUnknownStrategy() {
        assertSame(ExecutionStrategy.BALANCED, ExecutionStrategy.from("unknown"));
    }
}
