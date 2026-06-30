package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C 端公告 VO。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppAnnouncementVO {

    /** 主键 ID */
    private Long id;
    /** 标题。 */
    private String title;
    /** body。 */
    private String body;
    /** publishAt。 */
    private LocalDateTime publishAt;

    /** 排序权重 */
    private Integer priority;
}
