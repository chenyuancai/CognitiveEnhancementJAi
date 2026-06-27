package cn.cyc.ai.cog.platform.operations.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ops_message_template")
public class MessageTemplateEntity extends BaseEntity {

    private String templateCode;
    private String templateName;
    private String channel;
    private String content;
    @TableField("variable_schema")
    private String variableSchema;
    private String status;
}
