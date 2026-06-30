package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 内容新增/编辑请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentSaveRequest {

    /** 主键 ID */
    private Long id;

    /** 标题。 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 类型。 */
    @NotBlank(message = "内容类型不能为空")
    private String type;

    /** author。 */
    private String author;

    /** 摘要。 */
    private String summary;

    /** body。 */
    private String body;

    /** 可读最低会员等级。 */
    private String minLevelCode;
}
