package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

/**
 * 消息Template视图对象
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MessageTemplateVO {

    /** 主键 ID */
    private Long id;
    /** template编码。 */
    private String templateCode;
    /** template名称。 */
    private String templateName;
    /** channel。 */
    private String channel;
    /** 内容。 */
    private String content;
    /** variableSchema。 */
    private String variableSchema;
    /** 状态。 */
    private String status;
}
