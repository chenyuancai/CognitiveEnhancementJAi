package cn.cyc.ai.cog.runtime.observation.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 执行链路记录。
 *
 * @param traceId        链路标识
 * @param tenantCode     租户编码
 * @param capabilityCode 能力编码
 * @param agentCode      Agent 编码
 * @param resultStatus   执行结果状态
 * @param success        是否成功完成执行
 * @param failureReason  执行失败原因（成功时为 null）
 * @param recordedAt     记录时间
 * @param input          执行输入摘要
 * @param routing        路由装配摘要
 * @param result         执行结果摘要
 * @author cyc
 */
public record ExecutionRecord(String traceId,
                              String tenantCode,
                              String capabilityCode,
                              String capabilityVersion,
                              String agentCode,
                              String resultStatus,
                              boolean success,
                              String failureReason,
                              Instant recordedAt,
                              ExecutionInputDetail input,
                              ExecutionRoutingDetail routing,
                              ExecutionResultDetail result) {

    public ExecutionRecord {
        tenantCode = TenantContext.normalize(tenantCode);
    }

    public ExecutionRecord(String traceId,
                           String capabilityCode,
                           String agentCode,
                           String resultStatus,
                           boolean success,
                           String failureReason,
                           Instant recordedAt,
                           ExecutionInputDetail input,
                           ExecutionRoutingDetail routing,
                           ExecutionResultDetail result) {
        this(traceId, TenantContext.DEFAULT_TENANT_CODE, capabilityCode, null, agentCode, resultStatus,
                success, failureReason, recordedAt, input, routing, result);
    }

    /**
     * 带租户编码的便捷构造（兼容旧调用）。
     */
    public ExecutionRecord(String traceId,
                           String tenantCode,
                           String capabilityCode,
                           String agentCode,
                           String resultStatus,
                           boolean success,
                           String failureReason,
                           Instant recordedAt,
                           ExecutionInputDetail input,
                           ExecutionRoutingDetail routing,
                           ExecutionResultDetail result) {
        this(traceId, tenantCode, capabilityCode, null, agentCode, resultStatus,
                success, failureReason, recordedAt, input, routing, result);
    }

    /**
     * 仅摘要字段的便捷构造（列表查询兼容）。
     */
    public ExecutionRecord(String traceId,
                           String capabilityCode,
                           String agentCode,
                           String resultStatus,
                           boolean success,
                           String failureReason,
                           Instant recordedAt) {
        this(traceId, TenantContext.DEFAULT_TENANT_CODE, capabilityCode, null, agentCode, resultStatus,
                success, failureReason, recordedAt, null, null, null);
    }

    /**
     * 执行输入摘要。
     */
    public record ExecutionInputDetail(
            Map<String, Object> params,
            Map<String, Object> parameters
    ) {
    }

    /**
     * 路由装配摘要。
     */
    public record ExecutionRoutingDetail(
            String capabilityCode,
            String capabilityName,
            String agentCode,
            String agentName,
            String promptCode,
            List<String> skillCodes,
            String modelCode
    ) {
    }

    /**
     * 执行结果摘要。
     */
    public record ExecutionResultDetail(
            String status,
            String message,
            List<String> allowedSkillCodes,
            Map<String, Object> output
    ) {
    }
}
