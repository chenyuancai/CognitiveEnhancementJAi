package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.center.common.CenterPageQuery;

/**
 * Agent 分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class AgentPageQuery extends CenterPageQuery {

    /**
     * 绑定模型编码。
     */
    private String modelCode;

    /**
     * 获取模型编码。
     * @return 模型编码
     */
    public String getModelCode() {
        return modelCode;
    }

    /**
     * 设置模型编码。
     *
     * @param modelCode 模型编码
     */
    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }
}
