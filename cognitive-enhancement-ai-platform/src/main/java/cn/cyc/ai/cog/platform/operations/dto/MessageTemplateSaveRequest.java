package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 消息TemplateSave请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MessageTemplateSaveRequest {

    /** 主键 ID */
    private Long id;

    /** template编码。 */
    @NotBlank
    private String templateCode;
    /** template名称。 */
    @NotBlank
    private String templateName;
    /** channel。 */
    @NotBlank
    private String channel;
    /** 内容。 */
    @NotBlank
    private String content;
    /** variableSchema。 */
    private String variableSchema;
    /** 状态。 */
    private String status;
}
