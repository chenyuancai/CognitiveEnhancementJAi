package cn.cyc.ai.cog.admin.content.dto;

import lombok.Data;

/**
 * 内容标签视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentTagVO {

    /** 主键 ID */
    private Long id;
    /** 标签名称。 */
    private String tagName;
    /** 标签Color。 */
    private String tagColor;
}
