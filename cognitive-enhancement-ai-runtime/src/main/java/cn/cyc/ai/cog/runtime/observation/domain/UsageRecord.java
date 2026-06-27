package cn.cyc.ai.cog.runtime.observation.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 用量记录占位对象。
 *
 * @param traceId             链路标识
 * @param tenantCode          租户编码
 * @param capabilityCode      能力编码
 * @param agentCode           Agent 编码
 * @param executorType        执行器类型
 * @param modelCode           模型编码
 * @param toolCode            Tool 编码
 * @param inputTokenCount     输入 token 数
 * @param outputTokenCount    输出 token 数
 * @param totalTokenCount     总 token 数
 * @param estimatedCostAmount 预估成本
 * @param recordedAt          记录时间
 * @author cyc
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
