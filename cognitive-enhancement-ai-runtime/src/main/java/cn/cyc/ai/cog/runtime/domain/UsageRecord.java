package cn.cyc.ai.cog.runtime.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 用量记录占位对象。
 *
 * @param traceId             链路标识
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
}
