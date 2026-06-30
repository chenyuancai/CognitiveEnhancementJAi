package cn.cyc.ai.cog.runtime.tool.adapter.spi;

import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;

/**
 * Tool Adapter SPI，按协议类型统一封装 Tool 调用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ToolAdapter {

    /**
     * 支持的 Tool 协议类型。
     *
     * @return 协议类型
     */
    ToolProtocolType protocolType();

    /**
     * 调用 Tool 并返回载荷。
     *
     * @param context 调用上下文
     * @return Tool 执行结果
     */
    Object invoke(ToolAdapterContext context);

    /**
     * 判断当前调用是否属于 mock/演示模式。
     *
     * @param tool Tool 定义
     * @return 是否 mock
     */
    default boolean mock(ToolDefinition tool) {
        return protocolType() != ToolProtocolType.HTTP;
    }
}
