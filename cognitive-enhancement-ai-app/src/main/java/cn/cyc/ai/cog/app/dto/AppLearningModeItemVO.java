package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * 单个学习模式项。
 */
@Data
public class AppLearningModeItemVO {

    private String mode;
    private String capabilityCode;
    private boolean enabled;
    private String reason;
}
