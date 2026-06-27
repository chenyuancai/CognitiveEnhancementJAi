package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.harness.OutputGovernance;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.domain.AgentRuntimeResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.AgentRuntime;
import cn.cyc.ai.cog.runtime.audit.spi.AuditRecorder;
import cn.cyc.ai.cog.runtime.spi.CapabilityRuntime;
import cn.cyc.ai.cog.runtime.observation.spi.ExecutionRecorder;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.spi.InputSchemaValidator;
import cn.cyc.ai.cog.runtime.observation.spi.UsageMeter;
import cn.cyc.ai.cog.runtime.usage.spi.RuntimeUsageAccountService;
import cn.cyc.ai.cog.runtime.release.router.CapabilityVersionResolver;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpanType;
import cn.cyc.ai.cog.runtime.trace.span.TraceSpanRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 默认能力运行时实现。
 *
 * @author cyc
 */
@Service
public class DefaultCapabilityRuntime implements CapabilityRuntime {

    /**
     * 运行时日志。
     */
    private static final Logger log = LoggerFactory.getLogger(DefaultCapabilityRuntime.class);

    /**
     * 能力定义仓储。
     */
    private final CapabilityDefinitionRepository capabilityDefinitionRepository;

    /**
     * Agent 运行时。
     */
    private final AgentRuntime agentRuntime;

    /**
     * 输入 Schema 校验器。
     */
    private final InputSchemaValidator inputSchemaValidator;

    /**
     * 执行记录器。
     */
    private final ExecutionRecorder executionRecorder;

    /**
     * 用量记录器。
     */
    private final UsageMeter usageMeter;

    /**
     * 输出治理器。
     */
    private final OutputGovernance outputGovernance;

    /**
     * 用量额度账户服务。
     */
    private final RuntimeUsageAccountService runtimeUsageAccountService;

    /**
     * 能力版本解析器。
     */
    private final CapabilityVersionResolver capabilityVersionResolver;

    /**
     * 审计记录器。
     */
    private final AuditRecorder auditRecorder;

    private final TraceSpanRecorder traceSpanRecorder;

    /**
     * 构造默认能力运行时。
     *
     * @param capabilityDefinitionRepository 能力定义仓储
     * @param agentRuntime                   Agent 运行时
     * @param inputSchemaValidator          输入 Schema 校验器
     * @param executionRecorder             执行记录器
     * @param usageMeter                    用量记录器
     * @param outputGovernance              输出治理器
     * @param runtimeUsageAccountService    用量额度账户服务
     * @param capabilityVersionResolver     能力版本解析器
     * @param auditRecorder                 审计记录器
     */
    public DefaultCapabilityRuntime(CapabilityDefinitionRepository capabilityDefinitionRepository,
                                    AgentRuntime agentRuntime,
                                    InputSchemaValidator inputSchemaValidator,
                                    ExecutionRecorder executionRecorder,
                                    UsageMeter usageMeter,
                                    OutputGovernance outputGovernance,
                                    RuntimeUsageAccountService runtimeUsageAccountService,
                                    CapabilityVersionResolver capabilityVersionResolver,
                                    AuditRecorder auditRecorder,
                                    TraceSpanRecorder traceSpanRecorder) {
        this.capabilityDefinitionRepository = capabilityDefinitionRepository;
        this.agentRuntime = agentRuntime;
        this.inputSchemaValidator = inputSchemaValidator;
        this.executionRecorder = executionRecorder;
        this.usageMeter = usageMeter;
        this.outputGovernance = outputGovernance;
        this.runtimeUsageAccountService = runtimeUsageAccountService;
        this.capabilityVersionResolver = capabilityVersionResolver;
        this.auditRecorder = auditRecorder;
        this.traceSpanRecorder = traceSpanRecorder;
    }

    /**
     * 执行能力运行时入口。
     *
     * @param request 能力执行请求
     * @return 能力执行响应
     */
    @Override
    public CapabilityExecuteResponse execute(CapabilityExecuteRequest request) {
        log.info("收到能力执行请求, traceId={}, capabilityCode={}",
                TraceContext.getTraceId(), request.capabilityCode());
        CapabilityDefinition capability = capabilityVersionResolver.resolve(
                request.capabilityCode(), TraceContext.getTraceId());
        inputSchemaValidator.validate(request, capability);

        ExecutionContext context = new ExecutionContext(
                TraceContext.getTraceId(),
                request,
                capability,
                null,
                null,
                null,
                Map.of()
        );
        TraceSpanRecorder.SpanScope capabilitySpan = traceSpanRecorder.open(
                context.traceId(),
                TraceSpanType.CAPABILITY,
                capability.capabilityCode(),
                Map.of("version", capability.version()));
        try {
            AgentRuntimeResult runtimeResult;
            try {
                runtimeResult = agentRuntime.execute(context);
            } catch (RuntimeException ex) {
                traceSpanRecorder.fail(capabilitySpan, ex, Map.of("failureReason", ex.getMessage()));
                executionRecorder.recordFailure(context, ex.getMessage());
                recordRuntimeFailure(context, ex.getMessage(), ex);
                log.warn("能力执行失败, traceId={}, capabilityCode={}, failureReason={}",
                        context.traceId(), capability.capabilityCode(), ex.getMessage());
                throw ex;
            }
            ExecutionResult executionResult = runtimeResult.result();
            traceSpanRecorder.succeed(capabilitySpan, Map.of("resultStatus", executionResult.status()));
            executionRecorder.record(runtimeResult.context(), executionResult);
            UsageRecord usageRecord = usageMeter.record(runtimeResult.context(), executionResult);
            runtimeUsageAccountService.recordUsage(usageRecord);
            recordRuntimeInvocation(runtimeResult.context(), executionResult);
            log.info("能力执行请求完成基础路由, traceId={}, capabilityCode={}, agentCode={}, resultStatus={}",
                    runtimeResult.context().traceId(),
                    capability.capabilityCode(),
                    runtimeResult.context().agent().agentCode(),
                    executionResult.status());

            ExecutionResult governedResult = outputGovernance.govern(executionResult, null);

            return new CapabilityExecuteResponse(
                    runtimeResult.context().traceId(),
                    capability,
                    runtimeResult.context().agent(),
                    governedResult
            );
        } finally {
            traceSpanRecorder.clear();
        }
    }

    private void recordRuntimeInvocation(ExecutionContext context, ExecutionResult result) {
        try {
            auditRecorder.recordRuntimeInvocation(context, result);
        } catch (RuntimeException ex) {
            log.warn("记录运行调用审计失败, traceId={}, capabilityCode={}",
                    context.traceId(), context.capability().capabilityCode(), ex);
        }
    }

    private void recordRuntimeFailure(ExecutionContext context, String failureReason, Throwable cause) {
        try {
            auditRecorder.recordRuntimeFailure(context, failureReason, cause);
        } catch (RuntimeException ex) {
            log.warn("记录运行失败审计失败, traceId={}, capabilityCode={}, failureReason={}",
                    context.traceId(), context.capability().capabilityCode(), failureReason, ex);
        }
    }
}
