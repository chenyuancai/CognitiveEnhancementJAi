package cn.cyc.ai.cog.platform.operations.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MessageTemplatePreviewRequest {

    private Long id;

    private Map<String, Object> params;
}
