package cn.cyc.ai.cog.runtime.tool;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.api.ToolInvocationRequest;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.tool.adapter.ToolAdapterRegistry;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapter;
import cn.cyc.ai.cog.runtime.tool.adapter.spi.ToolAdapterContext;
import cn.cyc.ai.cog.runtime.tool.spi.ToolInputSchemaValidator;
import cn.cyc.ai.cog.runtime.tool.spi.ToolRuntime;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 默认 ToolRuntime 实现，通过 Tool Adapter 注册表支持 JAVA_LOCAL、HTTP 与 MCP 协议。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class DefaultToolRuntime implements ToolRuntime {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(DefaultToolRuntime.class);

    /** 工具Definition仓储。 */
    private final ToolDefinitionRepository toolDefinitionRepository;
    /** 工具输入Schema校验器。 */
    private final ToolInputSchemaValidator toolInputSchemaValidator;
    /** 工具AdapterRegistry。 */
    private final ToolAdapterRegistry toolAdapterRegistry;
    /** 链路SpanRecorder。 */
    private final TraceSpanRecorder traceSpanRecorder;

    /**
     * 创建DefaultToolRuntime。
     */
    public DefaultToolRuntime(ToolDefinitionRepository toolDefinitionRepository,
                              ToolInputSchemaValidator toolInputSchemaValidator,
                              ToolAdapterRegistry toolAdapterRegistry,
                              TraceSpanRecorder traceSpanRecorder) {
        this.toolDefinitionRepository = toolDefinitionRepository;
        this.toolInputSchemaValidator = toolInputSchemaValidator;
        this.toolAdapterRegistry = toolAdapterRegistry;
        this.traceSpanRecorder = traceSpanRecorder;
    }

    /**
     * 执行操作。
     *
     * @param context 上下文
     * @param toolCode 工具编码
     * @param input 输入
     * @return 执行结果
     */
    @Override
    public ToolInvocationResult invoke(ExecutionContext context, String toolCode, Object input) {
        return invokeInternal(context, toolCode, input, true);
    }

    /**
     * 执行invokeDebug。
     *
     * @param context 上下文
     * @param toolCode 工具编码
     * @param input 输入
     * @return 执行结果
     */
    @Override
    public ToolInvocationResult invokeDebug(ExecutionContext context, String toolCode, Object input) {
        return invokeInternal(context, toolCode, input, false);
    }

    /**
     * 执行invokeInternal。
     * @return 执行结果
     */
    private ToolInvocationResult invokeInternal(ExecutionContext context,
                                                String toolCode,
                                                Object input,
                                                boolean enforceSkillBinding) {
        ToolDefinition tool = toolDefinitionRepository.findByCode(toolCode)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Tool: " + toolCode));
        if (tool.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "Tool 未启用: " + toolCode);
        }
        if (enforceSkillBinding) {
            assertToolAllowed(context, tool);
        }

        toolInputSchemaValidator.validate(input, tool);

        ToolInvocationRequest invocationRequest = new ToolInvocationRequest(
                context.traceId(),
                context.capability().capabilityCode(),
                context.agent().agentCode(),
                tool.toolCode(),
                tool.protocolType().name(),
                input,
                context.request().parameters()
        );
        log.info("执行 ToolRuntime 调用, traceId={}, capabilityCode={}, toolCode={}, protocol={}, parameterKeys={}",
                context.traceId(),
                context.capability().capabilityCode(),
                toolCode,
                tool.protocolType(),
                invocationRequest.parameters().keySet());

        ToolAdapter adapter = toolAdapterRegistry.get(tool.protocolType());
        TraceSpanRecorder.SpanScope toolSpan = enforceSkillBinding
                ? traceSpanRecorder.open(context.traceId(), TraceSpanType.TOOL, toolCode,
                Map.of("protocol", tool.protocolType().name(), "adapter", adapter.getClass().getSimpleName()))
                : null;
        try {
            boolean mock = adapter.mock(tool);
            Object payload = invokeWithRetry(tool, () -> adapter.invoke(new ToolAdapterContext(context, tool, invocationRequest)));
            ToolInvocationResult result = new ToolInvocationResult(
                    "TOOL",
                    tool.toolCode(),
                    tool.protocolType().name(),
                    tool.permissionScope(),
                    tool.riskLevel().name(),
                    input,
                    invocationRequest.parameters(),
                    payload,
                    mock
            );
            if (toolSpan != null) {
                traceSpanRecorder.succeed(toolSpan, Map.of("mock", mock));
            }
            return result;
        } catch (RuntimeException ex) {
            if (toolSpan != null) {
                traceSpanRecorder.fail(toolSpan, ex, null);
            }
            throw ex;
        }
    }

    /**
     * 执行assert工具Allowed。
     *
     * @param context 上下文
     * @param tool 工具
     */
    private void assertToolAllowed(ExecutionContext context, ToolDefinition tool) {
        boolean allowed = context.skills().stream()
                .anyMatch(skill -> skill.boundToolCodes().contains(tool.toolCode()));
        if (!allowed) {
            throw new BusinessException("FORBIDDEN",
                    "当前 Agent 未授权调用 Tool: " + tool.toolCode());
        }
    }

    /**
     * 执行invokeWithRetry。
     *
     * @param tool 工具
     * @param invocation invocation
     * @return 执行结果
     */
    private Object invokeWithRetry(ToolDefinition tool, Supplier<Object> invocation) {
        int maxAttempts = Math.max(1, tool.retryPolicy().maxAttempts());
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return invocation.get();
            } catch (RuntimeException ex) {
                lastException = ex;
                if (attempt >= maxAttempts) {
                    break;
                }
                log.warn("Tool 调用失败，准备重试, toolCode={}, attempt={}, maxAttempts={}, reason={}",
                        tool.toolCode(), attempt, maxAttempts, ex.getMessage());
            }
        }
        throw lastException;
    }
}
