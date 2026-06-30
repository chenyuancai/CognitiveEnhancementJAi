package cn.cyc.ai.cog.app.tutoring.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅导回答治理校验结果。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringGovernanceResult {

    /** 治理后的回答文本。 */
    private String answer;

    /** 是否触发违规。 */
    private boolean violated;

    /** 违规项说明列表。 */
    private List<String> violations = new ArrayList<>();

    /**
     * 构建透传通过结果（不做治理改写）。
     *
     * @param answer 原始回答文本
     * @return 未违规的治理结果
     */
    public static AppTutoringGovernanceResult passThrough(String answer) {
        AppTutoringGovernanceResult result = new AppTutoringGovernanceResult();
        result.setAnswer(answer);
        result.setViolated(false);
        return result;
    }
}
