package cn.cyc.ai.cog.runtime.tool.spi;

import java.util.Map;

/**
 * MCP Tool 客户端 SPI，负责与 MCP Server 交互。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface McpToolClient {

    /**
     * 调用 MCP Tool。
     *
     * @param serverRef  MCP Server 引用（如 local / http://host:port）
     * @param toolName   MCP 工具名称
     * @param arguments  工具参数
     * @param parameters 透传执行参数
     * @return 工具执行结果
     */
    Object callTool(String serverRef, String toolName, Object arguments, Map<String, Object> parameters);
}
