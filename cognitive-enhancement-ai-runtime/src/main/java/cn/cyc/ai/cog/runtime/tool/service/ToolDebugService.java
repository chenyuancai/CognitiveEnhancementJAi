package cn.cyc.ai.cog.runtime.tool.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.prompt.PromptTemplate;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.runtime.audit.spi.AuditRecorder;
import cn.cyc.ai.cog.runtime.tool.dto.ToolDebugInvokeRequest;
import cn.cyc.ai.cog.runtime.tool.dto.ToolDebugInvokeResponse;
import cn.cyc.ai.cog.runtime.tool.spi.ToolRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tool 调试调用服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ToolDebugService {

    /**
     * 服务日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ToolDebugService.class);

    /**
     * Tool 定义仓储。
     */
    private final ToolDefinitionRepository toolDefinitionRepository;

    /**
     * Tool 运行时。
     */
    private final ToolRuntime toolRuntime;

    /**
     * 审计记录器。
     */
    private final AuditRecorder auditRecorder;

    /**
     * 构造 Tool 调试调用服务。
     *
     * @param toolDefinitionRepository Tool 定义仓储
     * @param toolRuntime              Tool 运行时
     * @param auditRecorder            审计记录器
     */
    public ToolDebugService(ToolDefinitionRepository toolDefinitionRepository,
                            ToolRuntime toolRuntime,
                            AuditRecorder auditRecorder) {
        this.toolDefinitionRepository = toolDefinitionRepository;
        this.toolRuntime = toolRuntime;
        this.auditRecorder = auditRecorder;
    }

    /**
     * 调试调用指定 Tool。
     *
     * @param toolCode             Tool 编码
     * @param request              调试请求
     * @param headerDebugConfirmed 请求头确认标识
     * @return 调试调用响应
     */
    public ToolDebugInvokeResponse debugInvoke(String toolCode,
                                               ToolDebugInvokeRequest request,
                                               Boolean headerDebugConfirmed) {
        ToolDebugInvokeRequest actualRequest = request == null
                ? new ToolDebugInvokeRequest(toolCode, null, Map.of(), null, null)
                : request;
        String traceId = resolveTraceId(actualRequest.traceId());
        TraceContext.setTraceId(traceId);
        long startedAt = System.nanoTime();
        boolean success = false;
        String failureReason = null;
        try {
            ToolDefinition tool = toolDefinitionRepository.findByCode(toolCode)
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到 Tool: " + toolCode));
            if (tool.status() != CommonStatus.ENABLED) {
                throw new BusinessException("CONFLICT", "Tool 未启用: " + toolCode);
            }
            if (tool.riskLevel() == RiskLevel.HIGH && !isDebugConfirmed(actualRequest, headerDebugConfirmed)) {
                throw new BusinessException("CONFIRMATION_REQUIRED", "HIGH 风险 Tool 调试需要 debugConfirmed=true");
            }

            ExecutionContext context = debugContext(traceId, toolCode, actualRequest);
            ToolInvocationResult result = toolRuntime.invokeDebug(context, toolCode, actualRequest.input());
            success = true;
            long latencyMs = elapsedMillis(startedAt);
            return new ToolDebugInvokeResponse(
                    traceId,
                    result.toolCode(),
                    result.protocolType(),
                    result.riskLevel(),
                    result.mock(),
                    latencyMs,
                    result
            );
        } catch (RuntimeException ex) {
            failureReason = ex.getMessage();
            throw ex;
        } finally {
            recordAudit(traceId, toolCode, success, elapsedMillis(startedAt), failureReason);
        }
    }

    /**
     * 判断是否为DebugConfirmed。
     *
     * @param request 请求
     * @param headerDebugConfirmed headerDebugConfirmed
     * @return 是否满足条件
     */
    private boolean isDebugConfirmed(ToolDebugInvokeRequest request, Boolean headerDebugConfirmed) {
        return Boolean.TRUE.equals(request.debugConfirmed()) || Boolean.TRUE.equals(headerDebugConfirmed);
    }

    /**
     * 执行resolve链路ID。
     *
     * @param requestTraceId 请求链路ID
     * @return 执行结果
     */
    private String resolveTraceId(String requestTraceId) {
        if (StringUtils.hasText(requestTraceId)) {
            return requestTraceId;
        }
        if (StringUtils.hasText(TraceContext.getTraceId())) {
            return TraceContext.getTraceId();
        }
        return "trace-tool-debug-" + UUID.randomUUID();
    }

    /**
     * 执行debug上下文。
     *
     * @param traceId 链路 Trace ID
     * @param toolCode 工具编码
     * @param request 请求
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    private ExecutionContext debugContext(String traceId, String toolCode, ToolDebugInvokeRequest request) {
        Map<String, Object> input = request.input() instanceof Map<?, ?> map
                ? (Map<String, Object>) map
                : singletonInput(request.input());
        CapabilityExecuteRequest executeRequest = new CapabilityExecuteRequest(
                "tool.debug." + toolCode,
                input,
                request.parameters()
        );
        SchemaDefinition schema = new SchemaDefinition("object", "Tool 调试占位 Schema", true, Map.of(), null, List.of());
        CapabilityDefinition capability = new CapabilityDefinition(
                "tool.debug." + toolCode,
                "Tool 调试调用",
                "管理台 Tool 调试调用占位能力",
                schema,
                schema,
                Map.of(),
                ExecutionMode.SYNC,
                "agent.tool.debug",
                RiskLevel.LOW,
                false,
                CommonStatus.ENABLED
        );
        AgentDefinition agent = new AgentDefinition(
                "agent.tool.debug",
                "Tool 调试 Agent",
                "管理台 Tool 调试上下文",
                "仅用于单 Tool 调试调用",
                "tool-debug-model",
                1,
                BigDecimal.ZERO,
                1_000,
                List.of(),
                Map.of(),
                CommonStatus.ENABLED
        );
        PromptTemplate prompt = new PromptTemplate(
                "prompt.tool.debug",
                "Tool 调试 Prompt",
                "tool-debug",
                "v1",
                "",
                schema,
                schema,
                CommonStatus.ENABLED,
                Instant.now()
        );
        return new ExecutionContext(traceId, executeRequest, capability, agent, prompt, List.of(), Map.of());
    }

    /**
     * 执行recordAudit。
     *
     * @param traceId 链路 Trace ID
     * @param toolCode 工具编码
     * @param success 成功
     * @param latencyMs latencyMs
     * @param failureReason 失败原因
     */
    private void recordAudit(String traceId, String toolCode, boolean success, long latencyMs, String failureReason) {
        try {
            auditRecorder.recordToolDebugInvocation(traceId, toolCode, success, latencyMs, failureReason);
        } catch (RuntimeException ex) {
            log.warn("记录 Tool 调试审计失败, traceId={}, toolCode={}", traceId, toolCode, ex);
        }
    }

    /**
     * 执行elapsedMillis。
     *
     * @param startedAt startedAt
     * @return 执行结果
     */
    private long elapsedMillis(long startedAt) {
        return Math.max(0, (System.nanoTime() - startedAt) / 1_000_000);
    }

    private Map<String, Object> singletonInput(Object input) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("input", input);
        return values;
    }
}
