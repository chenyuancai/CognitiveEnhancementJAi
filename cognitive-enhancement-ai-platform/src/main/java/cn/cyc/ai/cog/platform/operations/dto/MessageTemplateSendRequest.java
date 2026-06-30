package cn.cyc.ai.cog.platform.operations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 消息TemplateSend请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class MessageTemplateSendRequest {

    /** 主键 ID */
    private Long id;

    /** recipient。 */
    @NotBlank
    private String recipient;

    private Map<String, Object> params;
}
