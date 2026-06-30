package cn.cyc.ai.cog.runtime.tool.spi;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;

/**
 * Tool 入参 Schema 校验器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ToolInputSchemaValidator {

    /**
     * 按 Tool 请求 Schema 校验输入参数。
     *
     * @param input 工具输入
     * @param tool  Tool 定义
     */
    void validate(Object input, ToolDefinition tool);
}
