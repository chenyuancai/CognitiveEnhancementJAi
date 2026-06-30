package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * C 端内容摘要（列表用，不含正文）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppContentSummaryVO {

    /** 主键 ID */
    private Long id;
    /** 标题。 */
    private String title;
    /** 内容类型。 */
    private String contentType;
    /** author。 */
    private String author;
    /** 摘要。 */
    private String summary;
    /** min等级编码。 */
    private String minLevelCode;
    /** locked。 */
    private boolean locked;
}
