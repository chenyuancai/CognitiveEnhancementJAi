package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.common.CenterPageQuery;

/**
 * Prompt 分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class PromptPageQuery extends CenterPageQuery {

    /**
     * 场景编码。
     */
    private String scenarioCode;

    /**
     * 获取Scenario编码。
     * @return Scenario编码
     */
    public String getScenarioCode() {
        return scenarioCode;
    }

    /**
     * 设置Scenario编码。
     *
     * @param scenarioCode scenario编码
     */
    public void setScenarioCode(String scenarioCode) {
        this.scenarioCode = scenarioCode;
    }
}
