package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteRequest;

/**
 * 能力输入 Schema 校验器。
 *
 * @author cyc
 */
public interface InputSchemaValidator {

    /**
     * 按 Capability 输入 Schema 校验请求输入。
     *
     * @param request    能力执行请求
     * @param capability 能力定义
     */
    void validate(CapabilityExecuteRequest request, CapabilityDefinition capability);
}
