package cn.cyc.ai.cog.runtime.audit.service;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.repository.InMemoryAuditLogRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 默认审计记录器测试。
 *
 * @author cyc
 */
class LoggingAuditRecorderTest {

    /**
     * 清理上下文。
     */
    @AfterEach
    void tearDown() {
        TraceContext.clear();
        TenantContext.clear();
    }

    /**
     * 验证运行成功会写入审计日志。
     */
    @Test
    void shouldRecordRuntimeInvocationAudit() {
        TraceContext.setTraceId("trace-audit-runtime");
        TenantContext.setTenantCode("tenant-a");
        InMemoryAuditLogRepository repository = new InMemoryAuditLogRepository();
        LoggingAuditRecorder recorder = new LoggingAuditRecorder(repository);

        recorder.recordRuntimeInvocation(context(), new ExecutionResult(
                "LLM_GENERATED", "ok", List.of(), Map.of("executorType", "LLM")
        ));

        List<AuditLogRecord> records = repository.listAll();
        assertEquals(1, records.size());
        AuditLogRecord record = records.get(0);
        assertEquals("tenant-a", record.tenantCode());
        assertEquals("trace-audit-runtime", record.traceId());
        assertEquals("RUNTIME_INVOCATION", record.eventType());
        assertEquals("EXECUTE_SUCCESS", record.action());
        assertEquals("CapabilityDefinition", record.resourceType());
        assertEquals("capability.audit", record.resourceCode());
        assertTrue(record.success());
        assertEquals("LLM_GENERATED", record.detail().get("resultStatus"));
    }

    /**
     * 验证运行失败会写入审计日志。
     */
    @Test
    void shouldRecordRuntimeFailureAudit() {
        InMemoryAuditLogRepository repository = new InMemoryAuditLogRepository();
        LoggingAuditRecorder recorder = new LoggingAuditRecorder(repository);

        recorder.recordRuntimeFailure(context(), "模型调用超时");

        AuditLogRecord record = repository.listAll().get(0);
        assertEquals("RUNTIME_INVOCATION", record.eventType());
        assertEquals("EXECUTE_FAILURE", record.action());
        assertEquals(false, record.success());
        assertEquals("模型调用超时", record.detail().get("failureReason"));
    }

    /**
     * 验证运行失败且携带异常时写入 RUNTIME_FAILURE 与 errorStack。
     */
    @Test
    void shouldRecordRuntimeFailureWithErrorStackWhenCausePresent() {
        InMemoryAuditLogRepository repository = new InMemoryAuditLogRepository();
        LoggingAuditRecorder recorder = new LoggingAuditRecorder(repository);
        RuntimeException cause = new RuntimeException("模型调用超时");

        recorder.recordRuntimeFailure(context(), "模型调用超时", cause);

        AuditLogRecord record = repository.listAll().get(0);
        assertEquals("RUNTIME_FAILURE", record.action());
        assertEquals(false, record.success());
        assertEquals("模型调用超时", record.detail().get("failureReason"));
        assertTrue(record.detail().get("errorStack").toString().contains("RuntimeException"));
    }

    private ExecutionContext context() {
        SchemaDefinition schema = new SchemaDefinition("object", "schema", true, Map.of(), null, List.of());
        CapabilityDefinition capability = new CapabilityDefinition(
                "capability.audit",
                "审计能力",
                "用于审计测试",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.audit",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED
        );
        return new ExecutionContext(
                TraceContext.getTraceId(),
                new CapabilityExecuteRequest("capability.audit", Map.of("question", "hello"), Map.of()),
                capability,
                null,
                null,
                List.of(),
                Map.of()
        );
    }
}
