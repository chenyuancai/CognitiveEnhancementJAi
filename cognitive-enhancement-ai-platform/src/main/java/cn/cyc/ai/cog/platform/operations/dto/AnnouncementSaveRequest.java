package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementSaveRequest {

    private Long id;

    @NotBlank
    private String title;
    private String body;
    private String status;
    private LocalDateTime publishAt;
    /** 定向会员等级编码，逗号分隔；空表示不限等级。 */
    private String targetLevelCodes;
    /** 定向用户 ID，逗号分隔；空表示不限用户。 */
    private String targetUserIds;
}
