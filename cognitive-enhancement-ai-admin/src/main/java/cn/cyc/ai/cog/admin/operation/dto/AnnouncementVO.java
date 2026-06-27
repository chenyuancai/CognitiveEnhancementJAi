package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementVO {

    private Long id;
    private String title;
    private String body;
    private String status;
    private LocalDateTime publishAt;
    private String targetLevelCodes;
    private String targetUserIds;
}
