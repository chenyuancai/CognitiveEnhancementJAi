package cn.cyc.ai.cog.runtime.file.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 文件解析任务。
 *
 * @param tenantCode   租户编码
 * @param taskId       任务 ID
 * @param fileId       文件 ID
 * @param status       任务状态
 * @param parseResult  解析结果 JSON
 * @param errorMessage 错误信息
 * @param startedAt    开始时间
 * @param finishedAt   结束时间
 * @param recordedAt   记录时间
 * @author cyc
 */
public record FileParseTask(
        String tenantCode,
        String taskId,
        String fileId,
        FileParseTaskStatus status,
        String parseResult,
        String errorMessage,
        Instant startedAt,
        Instant finishedAt,
        Instant recordedAt
) {

    public FileParseTask {
        tenantCode = TenantContext.normalize(tenantCode);
    }
}
