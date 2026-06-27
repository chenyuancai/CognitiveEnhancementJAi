package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端内容详情。
 */
@Data
public class AppContentDetailVO {

    private Long id;
    private String title;
    private String contentType;
    private String author;
    private String summary;
    private String body;
    private String minLevelCode;
    private boolean locked;
    private String upgradeHint;
}
