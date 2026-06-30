package cn.cyc.ai.cog.runtime.observation.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 用量记录占位对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record UsageRecord(String traceId,
                          String tenantCode,
                          String capabilityCode,
                          String capabilityVersion,
                          String agentCode,
                          String executorType,
                          String modelCode,
                          String toolCode,
                          int inputTokenCount,
                          int outputTokenCount,
                          int totalTokenCount,
                          BigDecimal estimatedCostAmount,
                          Instant recordedAt) {

    public UsageRecord {
        tenantCode = TenantContext.normalize(tenantCode);
    }

    /**
     * 创建Usage记录。
     */
    public UsageRecord(String traceId,
                       String capabilityCode,
                       String agentCode,
                       String executorType,
                       String modelCode,
                       String toolCode,
                       int inputTokenCount,
                       int outputTokenCount,
                       int totalTokenCount,
                       BigDecimal estimatedCostAmount,
                       Instant recordedAt) {
        this(traceId, TenantContext.DEFAULT_TENANT_CODE, capabilityCode, null, agentCode, executorType, modelCode,
                toolCode, inputTokenCount, outputTokenCount, totalTokenCount, estimatedCostAmount, recordedAt);
    }

    /**
     * 带租户编码的便捷构造（兼容旧调用）。
     */
    public UsageRecord(String traceId,
                       String tenantCode,
                       String capabilityCode,
                       String agentCode,
                       String executorType,
                       String modelCode,
                       String toolCode,
                       int inputTokenCount,
                       int outputTokenCount,
                       int totalTokenCount,
                       BigDecimal estimatedCostAmount,
                       Instant recordedAt) {
        this(traceId, tenantCode, capabilityCode, null, agentCode, executorType, modelCode,
                toolCode, inputTokenCount, outputTokenCount, totalTokenCount, estimatedCostAmount, recordedAt);
    }
}
