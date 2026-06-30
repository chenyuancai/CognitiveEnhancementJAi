package cn.cyc.ai.cog.runtime.tool.validation;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.runtime.tool.spi.ToolInputSchemaValidator;
import cn.cyc.ai.cog.runtime.support.SchemaValueValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 默认 Tool 入参 Schema 校验器，执行前拦截非法参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class DefaultToolInputSchemaValidator implements ToolInputSchemaValidator {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DefaultToolInputSchemaValidator.class);

    private static final SchemaValueValidator VALUE_VALIDATOR =
            new SchemaValueValidator("input", "Tool 输入参数", false);

    /**
     * 校验参数。
     *
     * @param input 输入
     * @param tool 工具
     */
    @Override
    public void validate(Object input, ToolDefinition tool) {
        SchemaDefinition schema = tool.requestSchema();
        if (schema == null) {
            return;
        }
        if (!(input instanceof Map<?, ?> inputMap)) {
            throw new BusinessException("INVALID_ARGUMENT", "Tool 输入必须是 object");
        }
        log.info("开始校验 Tool 输入 Schema, toolCode={}, inputKeys={}", tool.toolCode(), inputMap.keySet());
        VALUE_VALIDATOR.validateRequiredProperties("input", inputMap, schema);
        VALUE_VALIDATOR.validateValue("input", inputMap, schema);
    }
}
