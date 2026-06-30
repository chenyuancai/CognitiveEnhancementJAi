package cn.cyc.ai.cog.runtime.audit.service;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.spi.AuditLogRepository;
import cn.cyc.ai.cog.runtime.audit.spi.AuditRecorder;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认审计记录器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class LoggingAuditRecorder implements AuditRecorder {

    /**
     * 审计日志。
     */
    private static final Logger log = LoggerFactory.getLogger(LoggingAuditRecorder.class);

    /**
     * 系统默认操作人。
     */
    private static final String SYSTEM_OPERATOR = "system";

    /**
     * 审计日志仓储。
     */
    private final AuditLogRepository auditLogRepository;

    /**
     * 构造默认审计记录器。
     *
     * @param auditLogRepository 审计日志仓储
     */
    public LoggingAuditRecorder(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * 记录运行时调用成功审计。
     *
     * @param context 运行时上下文
     * @param result  执行结果
     * @return 审计日志
     */
    @Override
    public AuditLogRecord recordRuntimeInvocation(ExecutionContext context, ExecutionResult result) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("resultStatus", result == null ? null : result.status());
        detail.put("message", result == null ? null : result.message());
        AuditLogRecord record = runtimeRecord(context, "EXECUTE_SUCCESS", true, detail);
        auditLogRepository.save(record);
        log.info("记录运行调用审计, traceId={}, capabilityCode={}, action={}",
                record.traceId(), record.resourceCode(), record.action());
        return record;
    }

    /**
     * 记录运行时调用失败审计。
     *
     * @param context       运行时上下文
     * @param failureReason 失败原因
     * @return 审计日志
     */
    @Override
    public AuditLogRecord recordRuntimeFailure(ExecutionContext context, String failureReason) {
        return recordRuntimeFailure(context, failureReason, null);
    }

    /**
     * 执行record运行时失败。
     *
     * @param context 上下文
     * @param failureReason 失败原因
     * @param cause cause
     * @return 执行结果
     */
    @Override
    public AuditLogRecord recordRuntimeFailure(ExecutionContext context, String failureReason, Throwable cause) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("failureReason", failureReason);
        if (cause != null) {
            detail.put("errorStack", TraceSpanSupport.truncateStackTrace(cause));
        }
        String action = cause == null ? "EXECUTE_FAILURE" : "RUNTIME_FAILURE";
        AuditLogRecord record = runtimeRecord(context, action, false, detail);
        auditLogRepository.save(record);
        log.warn("记录运行失败审计, traceId={}, capabilityCode={}, action={}, failureReason={}",
                record.traceId(), record.resourceCode(), action, failureReason);
        return record;
    }

    /**
     * 记录配置变更审计。
     *
     * @param action     操作动作
     * @param definition 元数据定义
     * @return 审计日志
     */
    @Override
    public AuditLogRecord recordConfigChange(String action, MetadataDefinition definition) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("resourceName", definition.name());
        AuditLogRecord record = new AuditLogRecord(
                TenantContext.currentTenantCode(),
                TraceContext.getTraceId(),
                "CONFIG_CHANGE",
                action,
                definition.getClass().getSimpleName(),
                definition.code(),
                SYSTEM_OPERATOR,
                true,
                detail,
                Instant.now()
        );
        auditLogRepository.save(record);
        log.info("记录配置变更审计, action={}, resourceType={}, resourceCode={}",
                action, record.resourceType(), record.resourceCode());
        return record;
    }

    /**
     * 记录 Tool 调试调用审计。
     *
     * @param traceId       链路追踪 ID
     * @param toolCode      Tool 编码
     * @param success       是否成功
     * @param latencyMs     调用耗时
     * @param failureReason 失败原因
     * @return 审计日志
     */
    @Override
    public AuditLogRecord recordToolDebugInvocation(String traceId,
                                                    String toolCode,
                                                    boolean success,
                                                    long latencyMs,
                                                    String failureReason) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("latencyMs", latencyMs);
        detail.put("failureReason", failureReason);
        AuditLogRecord record = new AuditLogRecord(
                TenantContext.currentTenantCode(),
                traceId,
                "TOOL_DEBUG_INVOKE",
                "DEBUG_INVOKE",
                "ToolDefinition",
                toolCode,
                SYSTEM_OPERATOR,
                success,
                detail,
                Instant.now()
        );
        auditLogRepository.save(record);
        log.info("记录 Tool 调试审计, traceId={}, toolCode={}, success={}, latencyMs={}",
                traceId, toolCode, success, latencyMs);
        return record;
    }

    /**
     * 执行运行时Record。
     * @return 执行结果
     */
    private AuditLogRecord runtimeRecord(ExecutionContext context,
                                         String action,
                                         boolean success,
                                         Map<String, Object> detail) {
        CapabilityDefinition capability = context == null ? null : context.capability();
        return new AuditLogRecord(
                TenantContext.currentTenantCode(),
                context == null ? TraceContext.getTraceId() : context.traceId(),
                "RUNTIME_INVOCATION",
                action,
                capability == null ? null : capability.getClass().getSimpleName(),
                capability == null ? null : capability.capabilityCode(),
                SYSTEM_OPERATOR,
                success,
                detail,
                Instant.now()
        );
    }
}
