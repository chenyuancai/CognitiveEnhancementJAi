package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端内容摘要（列表用，不含正文）。
 */
@Data
public class AppContentSummaryVO {

    private Long id;
    private String title;
    private String contentType;
    private String author;
    private String summary;
    private String minLevelCode;
    private boolean locked;
}
