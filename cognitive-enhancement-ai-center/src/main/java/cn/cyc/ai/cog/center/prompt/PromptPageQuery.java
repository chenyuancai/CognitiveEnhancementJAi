package cn.cyc.ai.cog.center.prompt;

import cn.cyc.ai.cog.center.common.CenterPageQuery;

/**
 * Prompt 分页查询参数。
 *
 * @author cyc
 */
public class PromptPageQuery extends CenterPageQuery {

    /**
     * 场景编码。
     */
    private String scenarioCode;

    public String getScenarioCode() {
        return scenarioCode;
    }

    public void setScenarioCode(String scenarioCode) {
        this.scenarioCode = scenarioCode;
    }
}
