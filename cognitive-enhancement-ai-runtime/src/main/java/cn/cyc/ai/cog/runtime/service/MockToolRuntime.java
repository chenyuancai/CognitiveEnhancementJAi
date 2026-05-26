package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.ToolInvocationRequest;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.ToolRuntime;
import cn.cyc.ai.cog.runtime.support.LocalToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一期默认 ToolRuntime mock 实现。
 *
 * @author cyc
 */
@Service
public class MockToolRuntime implements ToolRuntime {

    /**
     * 运行时日志。
     */
    private static final Logger log = LoggerFactory.getLogger(MockToolRuntime.class);

    /**
     * Tool 定义仓储。
     */
    private final ToolDefinitionRepository toolDefinitionRepository;

    /**
     * 本地 Tool 注册表。
     */
    private final LocalToolRegistry localToolRegistry;

    /**
     * 构造 ToolRuntime mock 实现。
     *
     * @param toolDefinitionRepository Tool 定义仓储
     * @param localToolRegistry        本地 Tool 注册表
     */
    public MockToolRuntime(ToolDefinitionRepository toolDefinitionRepository,
                           LocalToolRegistry localToolRegistry) {
        this.toolDefinitionRepository = toolDefinitionRepository;
        this.localToolRegistry = localToolRegistry;
    }

    /**
     * 执行一次 mock tool 调用。
     *
     * @param context  运行时上下文
     * @param toolCode 工具编码
     * @param input    工具输入
     * @return mock 输出
     */
    @Override
    public ToolInvocationResult invoke(ExecutionContext context, String toolCode, Object input) {
        ToolDefinition tool = toolDefinitionRepository.findByCode(toolCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Tool: " + toolCode));
        if (tool.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "Tool 未启用: " + toolCode);
        }

        ToolInvocationRequest invocationRequest = new ToolInvocationRequest(
                context.traceId(),
                context.capability().capabilityCode(),
                context.agent().agentCode(),
                tool.toolCode(),
                tool.protocolType().name(),
                input,
                context.request().parameters()
        );
        log.info("执行 ToolRuntime mock 调用, traceId={}, capabilityCode={}, toolCode={}, parameterKeys={}",
                context.traceId(),
                context.capability().capabilityCode(),
                toolCode,
                invocationRequest.parameters().keySet());

        return new ToolInvocationResult(
                "TOOL",
                tool.toolCode(),
                tool.protocolType().name(),
                tool.permissionScope(),
                input,
                invocationRequest.parameters(),
                resolveToolPayload(context, tool, invocationRequest),
                true
        );
    }

    /**
     * 解析 Tool 实际执行载荷。
     *
     * @param context 运行时上下文
     * @param tool    Tool 定义
     * @param request Tool 调用请求
     * @return 执行载荷
     */
    private Object resolveToolPayload(ExecutionContext context, ToolDefinition tool, ToolInvocationRequest request) {
        if (tool.protocolType() == ToolProtocolType.JAVA_LOCAL) {
            return localToolRegistry.find(tool.implRef())
                    .map(handler -> handler.invoke(context, request))
                    .orElseGet(() -> fallbackPayload(tool));
        }
        return fallbackPayload(tool);
    }

    /**
     * 构造回退执行载荷。
     *
     * @param tool Tool 定义
     * @return 回退输出
     */
    private Map<String, Object> fallbackPayload(ToolDefinition tool) {
        Map<String, Object> fallback = new LinkedHashMap<>();
        fallback.put("handler", "mock-fallback");
        fallback.put("implRef", tool.implRef());
        return fallback;
    }
}
