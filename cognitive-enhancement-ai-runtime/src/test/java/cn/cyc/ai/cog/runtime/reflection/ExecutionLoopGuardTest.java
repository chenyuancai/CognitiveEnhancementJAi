package cn.cyc.ai.cog.runtime.reflection;

import cn.cyc.ai.cog.core.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 执行循环防护测试。
 */
class ExecutionLoopGuardTest {

    private final LoopGuardProperties properties = new LoopGuardProperties();
    private final ExecutionLoopGuard guard = new ExecutionLoopGuard(properties);

    @AfterEach
    void tearDown() {
        guard.clear();
    }

    @Test
    void shouldAllowCallsWithinRepeatLimit() {
        properties.setMaxRepeat(3);
        assertDoesNotThrow(() -> {
            guard.check("trace-1", "llm:gpt-4o-mini");
            guard.check("trace-1", "llm:gpt-4o-mini");
            guard.check("trace-1", "llm:gpt-4o-mini");
        });
    }

    @Test
    void shouldRejectWhenRepeatLimitExceeded() {
        properties.setMaxRepeat(2);
        guard.check("trace-2", "tool:demo-echo");
        guard.check("trace-2", "tool:demo-echo");
        assertThrows(BusinessException.class, () -> guard.check("trace-2", "tool:demo-echo"));
    }
}
