package cn.cyc.ai.cog.platform.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 内容新增/编辑请求。
 *
 * @author cyc
 */
@Data
public class ContentSaveRequest {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容类型不能为空")
    private String type;

    private String author;

    private String summary;

    private String body;

    /** 可读最低会员等级。 */
    private String minLevelCode;
}
