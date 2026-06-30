package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Tool 分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class ToolPageQuery extends CenterPageQuery {

    /**
     * 协议类型。
     */
    private ToolProtocolType protocolType;

    /**
     * 风险等级。
     */
    private RiskLevel riskLevel;

    /**
     * 获取Protocol类型。
     * @return Protocol类型
     */
    public ToolProtocolType getProtocolType() {
        return protocolType;
    }

    /**
     * 设置Protocol类型。
     *
     * @param protocolType protocol类型
     */
    public void setProtocolType(ToolProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * 获取Risk等级。
     * @return Risk等级
     */
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    /**
     * 设置Risk等级。
     *
     * @param riskLevel risk等级
     */
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
