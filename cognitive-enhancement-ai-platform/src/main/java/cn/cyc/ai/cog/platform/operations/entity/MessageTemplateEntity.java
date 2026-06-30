package cn.cyc.ai.cog.platform.operations.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息Template实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ops_message_template")
public class MessageTemplateEntity extends BaseEntity {

    /** template编码。 */
    private String templateCode;
    /** template名称。 */
    private String templateName;
    /** channel。 */
    private String channel;
    /** 内容。 */
    private String content;
    /** variableSchema。 */
    @TableField("variable_schema")
    private String variableSchema;
    /** 状态。 */
    private String status;
}
