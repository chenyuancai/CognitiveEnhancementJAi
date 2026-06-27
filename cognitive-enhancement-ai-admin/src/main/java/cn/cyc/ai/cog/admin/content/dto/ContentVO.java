package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentVO {

    private Long id;
    private String title;
    private String contentType;
    private String author;
    private String status;
    private String summary;
    private String body;
    private String auditRemark;
    private String minLevelCode;
    private Integer currentVersion;
    private LocalDateTime publishedAt;
}
