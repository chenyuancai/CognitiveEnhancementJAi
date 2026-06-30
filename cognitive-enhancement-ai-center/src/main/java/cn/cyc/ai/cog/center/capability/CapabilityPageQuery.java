package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.common.CenterPageQuery;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

/**
 * Capability 分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 获取Bound智能体编码。
     * @return Bound智能体编码
     */
    public String getBoundAgentCode() {
        return boundAgentCode;
    }

    /**
     * 设置Bound智能体编码。
     *
     * @param boundAgentCode bound智能体编码
     */
    public void setBoundAgentCode(String boundAgentCode) {
        this.boundAgentCode = boundAgentCode;
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

    /**
     * 获取Execute模式。
     * @return Execute模式
     */
    public ExecutionMode getExecuteMode() {
        return executeMode;
    }

    /**
     * 设置Execute模式。
     *
     * @param executeMode execute模式
     */
    public void setExecuteMode(ExecutionMode executeMode) {
        this.executeMode = executeMode;
    }
}
