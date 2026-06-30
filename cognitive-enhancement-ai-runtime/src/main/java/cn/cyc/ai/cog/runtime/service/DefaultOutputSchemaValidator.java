package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.spi.OutputSchemaValidator;
import cn.cyc.ai.cog.runtime.support.SchemaValueValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 默认能力输出 Schema 校验器。
 * 当前版本只校验业务输出载荷 {@code businessOutput}，
 * 不把 runtime 附加元数据视为能力契约的一部分。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultOutputSchemaValidator implements OutputSchemaValidator {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DefaultOutputSchemaValidator.class);

    private static final SchemaValueValidator VALUE_VALIDATOR =
            new SchemaValueValidator("output", "输出参数", true);

    /**
     * 校验参数。
     *
     * @param capability 能力
     * @param executionResult execution结果
     */
    @Override
    public void validate(CapabilityDefinition capability, ExecutionResult executionResult) {
        SchemaDefinition outputSchema = capability.outputSchema();
        if (outputSchema == null) {
            return;
        }
        Object businessOutput = executionResult.output().get("businessOutput");
        log.info("开始校验能力输出 Schema, capabilityCode={}, resultStatus={}",
                capability.capabilityCode(),
                executionResult.status());
        VALUE_VALIDATOR.validateValue("output", businessOutput, outputSchema);
    }
}
