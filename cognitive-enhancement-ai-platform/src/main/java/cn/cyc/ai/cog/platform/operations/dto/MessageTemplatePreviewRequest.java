package cn.cyc.ai.cog.platform.operations.dto;

import lombok.Data;

import java.util.Map;

/**
 * 消息TemplatePreview请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MessageTemplatePreviewRequest {

    /** 主键 ID */
    private Long id;

    private Map<String, Object> params;
}
