package cn.cyc.ai.cog.admin.operation.dto;

import lombok.Data;

@Data
public class MessageTemplateVO {

    private Long id;
    private String templateCode;
    private String templateName;
    private String channel;
    private String content;
    private String variableSchema;
    private String status;
}
