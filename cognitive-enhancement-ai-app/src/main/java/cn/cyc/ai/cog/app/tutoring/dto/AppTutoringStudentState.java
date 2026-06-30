package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

/**
 * 本轮推断出的学生学习状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringStudentState {

    /** 当前知识点。 */
    private String knowledgePoint;

    /** 掌握程度。 */
    private String masteryLevel;

    /** 困惑点描述。 */
    private String confusion;

    /** 连续卡住次数。 */
    private int stuckCount;
}
