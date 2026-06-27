package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容版本摘要 VO。
 */
@Data
public class ContentVersionVO {

    private int versionNo;
    private String title;
    private String minLevelCode;
    private Long operatorId;
    private LocalDateTime createTime;
}
