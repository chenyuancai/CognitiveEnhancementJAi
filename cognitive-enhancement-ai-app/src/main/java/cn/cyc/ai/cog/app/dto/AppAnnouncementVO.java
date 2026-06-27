package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C 端公告 VO。
 */
@Data
public class AppAnnouncementVO {

    private Long id;
    private String title;
    private String body;
    private LocalDateTime publishAt;
}
