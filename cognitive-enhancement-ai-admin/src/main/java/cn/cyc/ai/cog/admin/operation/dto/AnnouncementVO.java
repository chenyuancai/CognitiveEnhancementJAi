package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Announcement视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AnnouncementVO {

    /** 主键 ID */
    private Long id;
    /** 标题。 */
    private String title;
    /** body。 */
    private String body;
    /** 状态。 */
    private String status;
    /** publishAt。 */
    private LocalDateTime publishAt;
    /** 目标等级Codes。 */
    private String targetLevelCodes;
    /** 目标用户Ids。 */
    private String targetUserIds;
}
