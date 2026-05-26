package cn.cyc.ai.cog.runtime.domain;

import java.time.Instant;

/**
 * 执行链路记录。
 *
 * @param traceId        链路标识
 * @param capabilityCode 能力编码
 * @param agentCode      Agent 编码
 * @param resultStatus   执行结果状态
 * @param success        是否成功完成执行
 * @param recordedAt     记录时间
 * @author cyc
 */
public record ExecutionRecord(String traceId,
                              String capabilityCode,
                              String agentCode,
                              String resultStatus,
                              boolean success,
                              Instant recordedAt) {
}
