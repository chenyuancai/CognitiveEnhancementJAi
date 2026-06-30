package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 内容标签Save请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentTagSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 标签名称。 */
    @NotBlank
    private String tagName;
    /** 标签Color。 */
    private String tagColor;
}
