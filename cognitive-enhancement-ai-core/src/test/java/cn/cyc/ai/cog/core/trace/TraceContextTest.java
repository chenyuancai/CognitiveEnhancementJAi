package cn.cyc.ai.cog.core.trace;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraceContextTest {

    private final TraceIdGenerator generator = new DefaultTraceIdGenerator();

    @AfterEach
    void tearDown() {
        TraceContext.clear();
    }

    @Test
    void shouldBindAndClearTraceId() {
        TraceContext.setTraceId("trace-fixed");

        assertEquals("trace-fixed", TraceContext.getTraceId());

        TraceContext.clear();

        assertNull(TraceContext.getTraceId());
    }

    @Test
    void shouldGenerateTraceIdWhenAbsent() {
        String traceId = TraceContext.getOrCreateTraceId(generator);

        assertNotNull(traceId);
        assertEquals(traceId, TraceContext.getTraceId());
        assertTrue(traceId.length() >= 16);
    }

    @Test
    void shouldReuseExistingTraceIdWhenPresent() {
        TraceContext.setTraceId("trace-existing");

        String traceId = TraceContext.getOrCreateTraceId(generator);

        assertEquals("trace-existing", traceId);
    }
}
