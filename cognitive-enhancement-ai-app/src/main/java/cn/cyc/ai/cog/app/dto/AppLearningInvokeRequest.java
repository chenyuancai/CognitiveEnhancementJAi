package cn.cyc.ai.cog.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * C 端学习 AI 调用请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppLearningInvokeRequest {

    /** 模式。 */
    @NotBlank(message = "mode 不能为空")
    private String mode;

    private Map<String, Object> input;

    private Map<String, Object> parameters;
}
