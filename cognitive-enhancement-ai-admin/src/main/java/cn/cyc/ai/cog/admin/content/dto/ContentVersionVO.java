package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容版本摘要 VO。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentVersionVO {

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
