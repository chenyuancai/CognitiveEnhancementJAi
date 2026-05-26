package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.exception.BusinessException;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteRequest;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteResponse;
import cn.cyc.ai.cog.runtime.api.ExecutionResult;
import cn.cyc.ai.cog.runtime.domain.AgentRuntimeResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.AgentRuntime;
import cn.cyc.ai.cog.runtime.spi.CapabilityRuntime;
import cn.cyc.ai.cog.runtime.spi.ExecutionRecorder;
import cn.cyc.ai.cog.runtime.spi.InputSchemaValidator;
import cn.cyc.ai.cog.runtime.spi.UsageMeter;
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
     * 构造默认能力运行时。
     *
     * @param capabilityDefinitionRepository 能力定义仓储
     * @param agentRuntime                   Agent 运行时
     * @param inputSchemaValidator          输入 Schema 校验器
     * @param executionRecorder             执行记录器
     * @param usageMeter                    用量记录器
     */
    public DefaultCapabilityRuntime(CapabilityDefinitionRepository capabilityDefinitionRepository,
                                    AgentRuntime agentRuntime,
                                    InputSchemaValidator inputSchemaValidator,
                                    ExecutionRecorder executionRecorder,
                                    UsageMeter usageMeter) {
        this.capabilityDefinitionRepository = capabilityDefinitionRepository;
        this.agentRuntime = agentRuntime;
        this.inputSchemaValidator = inputSchemaValidator;
        this.executionRecorder = executionRecorder;
        this.usageMeter = usageMeter;
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
        CapabilityDefinition capability = capabilityDefinitionRepository.findByCode(request.capabilityCode())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "未找到能力: " + request.capabilityCode()));
        if (capability.status() != CommonStatus.ENABLED) {
            throw new BusinessException("CONFLICT", "能力未启用: " + request.capabilityCode());
        }
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
        AgentRuntimeResult runtimeResult = agentRuntime.execute(context);
        ExecutionResult executionResult = runtimeResult.result();
        executionRecorder.record(runtimeResult.context(), executionResult);
        usageMeter.record(runtimeResult.context(), executionResult);
        log.info("能力执行请求完成基础路由, traceId={}, capabilityCode={}, agentCode={}, resultStatus={}",
                runtimeResult.context().traceId(),
                capability.capabilityCode(),
                runtimeResult.context().agent().agentCode(),
                executionResult.status());

        return new CapabilityExecuteResponse(
                runtimeResult.context().traceId(),
                capability,
                runtimeResult.context().agent(),
                executionResult
        );
    }
}
