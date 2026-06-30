package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

/**
 * 学习辅导下一步动作。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringNextAction {

    /** 动作类型。 */
    private String type;

    /** 动作说明。 */
    private String content;
}
