package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.runtime.api.ExecutionResult;

/**
 * 能力输出 Schema 校验器。
 *
 * @author cyc
 */
public interface OutputSchemaValidator {

    /**
     * 校验能力执行结果的业务输出是否符合能力定义的输出 Schema。
     *
     * @param capability    能力定义
     * @param executionResult 执行结果
     */
    void validate(CapabilityDefinition capability, ExecutionResult executionResult);
}
