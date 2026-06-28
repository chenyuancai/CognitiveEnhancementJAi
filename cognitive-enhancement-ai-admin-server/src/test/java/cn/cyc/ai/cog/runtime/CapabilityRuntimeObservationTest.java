package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.spi.AuditLogRepository;
import cn.cyc.ai.cog.runtime.observation.spi.ExecutionRecorder;
import cn.cyc.ai.cog.runtime.observation.spi.UsageMeter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 观测占位集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(RuntimeLlmTestDoubleConfiguration.class)
class CapabilityRuntimeObservationTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 审计日志仓储。
     */
    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 执行记录器 mock。
     */
    @MockBean
    private ExecutionRecorder executionRecorder;

    /**
     * 用量记录器 mock。
     */
    @MockBean
    private UsageMeter usageMeter;

    /**
     * 验证运行时链路会触发执行记录和用量记录。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRecordExecutionAndUsageAfterCapabilityExecution() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header("X-Trace-Id", "trace-test-observation-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "观测占位是否生效?"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        verify(executionRecorder, times(1)).record(any(ExecutionContext.class), any(ExecutionResult.class));
        verify(usageMeter, times(1)).record(any(ExecutionContext.class), any(ExecutionResult.class));
        boolean auditRecorded = auditLogRepository.listAll().stream()
                .anyMatch(this::isExpectedRuntimeAudit);
        assertTrue(auditRecorded);
    }

    private boolean isExpectedRuntimeAudit(AuditLogRecord record) {
        return "trace-test-observation-001".equals(record.traceId())
                && "RUNTIME_INVOCATION".equals(record.eventType())
                && "EXECUTE_SUCCESS".equals(record.action())
                && "capability.qa.answer".equals(record.resourceCode())
                && record.success();
    }
}
