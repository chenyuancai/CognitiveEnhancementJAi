package cn.cyc.ai.cog.app.dto;

import lombok.Data;

/**
 * 单个学习模式项。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppLearningModeItemVO {

    /** 模式。 */
    private String mode;
    /** 能力编码。 */
    private String capabilityCode;
    /** 是否启用。 */
    private boolean enabled;
    /** 原因。 */
    private String reason;

    /** 推荐入口路径（契约迁移提示）。 */
    private String recommendedPath;
}
