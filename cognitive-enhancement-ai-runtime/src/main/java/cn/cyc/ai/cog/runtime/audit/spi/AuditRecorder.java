package cn.cyc.ai.cog.runtime.audit.spi;

import cn.cyc.ai.cog.core.metadata.MetadataDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;

/**
 * 审计记录器。
 *
 * @author cyc
 */
public interface AuditRecorder {

    /**
     * 记录运行时调用成功审计。
     *
     * @param context 运行时上下文
     * @param result  执行结果
     * @return 审计日志
     */
    AuditLogRecord recordRuntimeInvocation(ExecutionContext context, ExecutionResult result);

    /**
     * 记录运行时调用失败审计。
     *
     * @param context       运行时上下文
     * @param failureReason 失败原因
     * @return 审计日志
     */
    AuditLogRecord recordRuntimeFailure(ExecutionContext context, String failureReason);

    /**
     * 记录运行时调用失败审计（含异常栈摘要）。
     *
     * @param context       运行时上下文
     * @param failureReason 失败原因
     * @param cause         失败异常
     * @return 审计日志
     */
    AuditLogRecord recordRuntimeFailure(ExecutionContext context, String failureReason, Throwable cause);

    /**
     * 记录配置变更审计。
     *
     * @param action     操作动作
     * @param definition 元数据定义
     * @return 审计日志
     */
    AuditLogRecord recordConfigChange(String action, MetadataDefinition definition);

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
    AuditLogRecord recordToolDebugInvocation(String traceId,
                                             String toolCode,
                                             boolean success,
                                             long latencyMs,
                                             String failureReason);
}
