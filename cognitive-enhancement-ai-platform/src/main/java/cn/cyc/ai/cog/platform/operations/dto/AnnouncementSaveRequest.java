package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AnnouncementSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AnnouncementSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 标题。 */
    @NotBlank
    private String title;
    /** body。 */
    private String body;
    /** 状态。 */
    private String status;
    /** publishAt。 */
    private LocalDateTime publishAt;
    /** 定向会员等级编码，逗号分隔；空表示不限等级。 */
    private String targetLevelCodes;
    /** 定向用户 ID，逗号分隔；空表示不限用户。 */
    private String targetUserIds;
}
