package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

/**
 * C 端 AI 助手对话状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringChatState {

    /** 是否需要用户继续回复。 */
    private boolean needUserReply;

    /** 下一步期望的用户动作。 */
    private String nextExpectedAction;

    /** 连续卡住次数。 */
    private int stuckCount;

    /** 当前掌握程度。 */
    private String masteryLevel;
}
