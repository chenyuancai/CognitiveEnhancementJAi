package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Capability 分页查询参数。
 *
 * @author cyc
 */
public class CapabilityPageQuery extends CenterPageQuery {

    /**
     * 绑定 Agent 编码。
     */
    private String boundAgentCode;

    /**
     * 风险等级。
     */
    private RiskLevel riskLevel;

    /**
     * 执行模式。
     */
    private ExecutionMode executeMode;

    public String getBoundAgentCode() {
        return boundAgentCode;
    }

    public void setBoundAgentCode(String boundAgentCode) {
        this.boundAgentCode = boundAgentCode;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public ExecutionMode getExecuteMode() {
        return executeMode;
    }

    public void setExecuteMode(ExecutionMode executeMode) {
        this.executeMode = executeMode;
    }
}
