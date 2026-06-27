package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.runtime.spi.InputSchemaValidator;
import cn.cyc.ai.cog.runtime.support.SchemaValueValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 默认能力输入 Schema 校验器。
 *
 * 当前版本优先校验已提供字段的类型与枚举约束，
 * 不在这一层重复处理 Prompt 必填变量缺失问题。
 *
 * @author cyc
 */
@Component
public class DefaultInputSchemaValidator implements InputSchemaValidator {

    private static final Logger log = LoggerFactory.getLogger(DefaultInputSchemaValidator.class);

    private static final SchemaValueValidator VALUE_VALIDATOR =
            new SchemaValueValidator("input", "输入参数", false);

    @Override
    public void validate(CapabilityExecuteRequest request, CapabilityDefinition capability) {
        SchemaDefinition inputSchema = capability.inputSchema();
        if (inputSchema == null) {
            return;
        }
        log.info("开始校验能力输入 Schema, capabilityCode={}, inputKeys={}",
                capability.capabilityCode(),
                request.input().keySet());
        VALUE_VALIDATOR.validateValue("input", request.input(), inputSchema);
    }
}
