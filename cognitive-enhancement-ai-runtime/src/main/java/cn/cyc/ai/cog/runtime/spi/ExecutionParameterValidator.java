package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteRequest;

/**
 * 执行参数校验器，负责在运行时入口对通用执行参数做收口校验。
 *
 * @author cyc
 */
public interface ExecutionParameterValidator {

    /**
     * 校验一次能力执行请求中的执行参数。
     *
     * @param request    能力执行请求
     * @param capability 能力定义
     * @param agent      Agent 定义
     */
    void validate(CapabilityExecuteRequest request, CapabilityDefinition capability, AgentDefinition agent);
}
