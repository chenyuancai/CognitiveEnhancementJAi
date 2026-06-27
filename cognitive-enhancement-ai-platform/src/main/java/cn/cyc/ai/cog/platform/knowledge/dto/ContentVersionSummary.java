package cn.cyc.ai.cog.platform.knowledge.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容版本摘要（管理端列表）。
 */
@Data
public class ContentVersionSummary {

    private int versionNo;
    private String title;
    private String minLevelCode;
    private Long operatorId;
    private LocalDateTime createTime;
}
