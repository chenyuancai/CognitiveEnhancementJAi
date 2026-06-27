package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C 端 Banner 展示 VO。
 */
@Data
public class AppBannerVO {

    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer sortNo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
