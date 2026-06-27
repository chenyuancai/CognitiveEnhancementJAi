package cn.cyc.ai.cog.runtime.tool.spi;

import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;

/**
 * Tool 运行时预留接口。
 *
 * @author cyc
 */
public interface ToolRuntime {

    /**
     * 调用指定 Tool。
     *
     * @param context  运行时上下文
     * @param toolCode 工具编码
     * @param input    工具输入
     * @return 工具输出
     */
    ToolInvocationResult invoke(ExecutionContext context, String toolCode, Object input);

    /**
     * 调试调用指定 Tool，不校验 Skill 绑定关系。
     *
     * @param context  运行时上下文
     * @param toolCode 工具编码
     * @param input    工具输入
     * @return 工具输出
     */
    ToolInvocationResult invokeDebug(ExecutionContext context, String toolCode, Object input);
}
