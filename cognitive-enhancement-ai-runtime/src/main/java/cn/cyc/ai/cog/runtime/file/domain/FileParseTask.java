package cn.cyc.ai.cog.runtime.file.domain;

import cn.cyc.ai.cog.runtime.security.TenantContext;

import java.time.Instant;

/**
 * 文件解析任务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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
