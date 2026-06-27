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
 */
@Service
public class DefaultToolRuntime implements ToolRuntime {

    private static final Logger log = LoggerFactory.getLogger(DefaultToolRuntime.class);

    private final ToolDefinitionRepository toolDefinitionRepository;
    private final ToolInputSchemaValidator toolInputSchemaValidator;
    private final ToolAdapterRegistry toolAdapterRegistry;
    private final TraceSpanRecorder traceSpanRecorder;

    public DefaultToolRuntime(ToolDefinitionRepository toolDefinitionRepository,
                              ToolInputSchemaValidator toolInputSchemaValidator,
                              ToolAdapterRegistry toolAdapterRegistry,
                              TraceSpanRecorder traceSpanRecorder) {
        this.toolDefinitionRepository = toolDefinitionRepository;
        this.toolInputSchemaValidator = toolInputSchemaValidator;
        this.toolAdapterRegistry = toolAdapterRegistry;
        this.traceSpanRecorder = traceSpanRecorder;
    }

    @Override
    public ToolInvocationResult invoke(ExecutionContext context, String toolCode, Object input) {
        return invokeInternal(context, toolCode, input, true);
    }

    @Override
    public ToolInvocationResult invokeDebug(ExecutionContext context, String toolCode, Object input) {
        return invokeInternal(context, toolCode, input, false);
    }

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

    private void assertToolAllowed(ExecutionContext context, ToolDefinition tool) {
        boolean allowed = context.skills().stream()
                .anyMatch(skill -> skill.boundToolCodes().contains(tool.toolCode()));
        if (!allowed) {
            throw new BusinessException("FORBIDDEN",
                    "当前 Agent 未授权调用 Tool: " + tool.toolCode());
        }
    }

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
