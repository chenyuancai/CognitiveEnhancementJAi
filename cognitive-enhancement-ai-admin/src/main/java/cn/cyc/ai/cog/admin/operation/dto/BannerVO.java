package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BannerVO {

    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer sortNo;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
