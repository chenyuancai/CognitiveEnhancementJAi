package cn.cyc.ai.cog.app.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * C端InC端消息视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppInAppMessageVO {

    /** 主键 ID */
    private Long id;
    /** template编码。 */
    private String templateCode;
    /** 标题。 */
    private String title;
    /** 内容。 */
    private String content;
    /** read。 */
    private boolean read;

    /** 分类展示名 */
    private String categoryLabel;

    /** 创建时间 */
    private LocalDateTime createTime;
}
