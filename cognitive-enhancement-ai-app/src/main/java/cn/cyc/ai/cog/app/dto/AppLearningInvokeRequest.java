package cn.cyc.ai.cog.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * C 端学习 AI 调用请求。
 */
@Data
public class AppLearningInvokeRequest {

    @NotBlank(message = "mode 不能为空")
    private String mode;

    private Map<String, Object> input;

    private Map<String, Object> parameters;
}
