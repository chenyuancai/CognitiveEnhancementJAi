package cn.cyc.ai.cog.platform.knowledge.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容版本摘要（管理端列表）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentVersionSummary {

    /** 版本号，每次更新递增 */
    private int versionNo;
    /** 标题。 */
    private String title;
    /** min等级编码。 */
    private String minLevelCode;
    /** operatorID */
    private Long operatorId;
    /** 创建时间 */
    private LocalDateTime createTime;
}
