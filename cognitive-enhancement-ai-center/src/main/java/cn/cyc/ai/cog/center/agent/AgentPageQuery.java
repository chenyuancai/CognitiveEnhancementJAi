package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.center.common.CenterPageQuery;

/**
 * Agent 分页查询参数。
 *
 * @author cyc
 */
public class AgentPageQuery extends CenterPageQuery {

    /**
     * 绑定模型编码。
     */
    private String modelCode;

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
}
