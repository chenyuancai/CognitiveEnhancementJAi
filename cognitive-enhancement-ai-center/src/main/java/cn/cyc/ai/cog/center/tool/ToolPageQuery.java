package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Tool 分页查询参数。
 *
 * @author cyc
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

    public ToolProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ToolProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
}
