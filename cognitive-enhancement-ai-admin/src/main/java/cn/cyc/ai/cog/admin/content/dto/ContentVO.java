package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentVO {

    /** 主键 ID */
    private Long id;
    /** 标题。 */
    private String title;
    /** 内容类型。 */
    private String contentType;
    /** author。 */
    private String author;
    /** 状态。 */
    private String status;
    /** 摘要。 */
    private String summary;
    /** body。 */
    private String body;
    /** auditRemark。 */
    private String auditRemark;
    /** min等级编码。 */
    private String minLevelCode;
    /** current版本号。 */
    private Integer currentVersion;
    /** publishedAt。 */
    private LocalDateTime publishedAt;
}
