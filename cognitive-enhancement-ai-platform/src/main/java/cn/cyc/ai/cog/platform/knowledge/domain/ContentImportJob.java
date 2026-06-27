package cn.cyc.ai.cog.platform.knowledge.domain;

import java.time.LocalDateTime;

public record ContentImportJob(
        Long id,
        Long tenantId,
        String fileName,
        String fileUrl,
        String sourceContent,
        String status,
        Integer totalCount,
        Integer successCount,
        Integer failCount,
        String resultJson,
        Long createBy,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
