package cn.cyc.ai.cog.runtime.observation.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * 执行链路记录。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 创建Execution记录。
     */
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
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record ExecutionInputDetail(
            Map<String, Object> params,
            Map<String, Object> parameters
    ) {
    }

    /**
     * 路由装配摘要。
     *
     * @author cyc
     * @date 2026/6/15 14:18
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
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public record ExecutionResultDetail(
            String status,
            String message,
            List<String> allowedSkillCodes,
            Map<String, Object> output
    ) {
    }
}
