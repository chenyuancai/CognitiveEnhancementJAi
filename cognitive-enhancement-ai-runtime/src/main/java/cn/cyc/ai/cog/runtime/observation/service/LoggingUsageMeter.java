package cn.cyc.ai.cog.runtime.observation.service;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.observation.spi.UsageRecordRepository;
import cn.cyc.ai.cog.runtime.observation.spi.UsageMeter;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.usage.service.RuntimeUsageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * 基于日志的默认用量记录器。
 *
 * @author cyc
 */
@Component
public class LoggingUsageMeter implements UsageMeter {

    /**
     * 用量日志。
     */
    private static final Logger log = LoggerFactory.getLogger(LoggingUsageMeter.class);

    /**
     * 用量记录仓储。
     */
    private final UsageRecordRepository usageRecordRepository;

    /**
     * 用量配置。
     */
    private final RuntimeUsageProperties usageProperties;

    /**
     * 构造基于日志的默认用量记录器。
     *
     * @param usageRecordRepository 用量记录仓储
     */
    public LoggingUsageMeter(UsageRecordRepository usageRecordRepository) {
        this(usageRecordRepository, new RuntimeUsageProperties());
    }

    /**
     * 构造基于日志的默认用量记录器。
     *
     * @param usageRecordRepository 用量记录仓储
     * @param usageProperties       用量配置
     */
    @Autowired
    public LoggingUsageMeter(UsageRecordRepository usageRecordRepository,
                             RuntimeUsageProperties usageProperties) {
        this.usageRecordRepository = usageRecordRepository;
        this.usageProperties = usageProperties;
    }

    /**
     * 记录一次 mock 用量摘要。
     *
     * @param context 运行时上下文
     * @param result  执行结果
     * @return 用量记录
     */
    @Override
    public UsageRecord record(ExecutionContext context, ExecutionResult result) {
        LlmInvocationResult llmInvocationResult = resolveLlmInvocationResult(result.output());
        UsageRecord record = new UsageRecord(
                context.traceId(),
                TenantContext.currentTenantCode(),
                context.capability().capabilityCode(),
                context.capability().version(),
                context.agent().agentCode(),
                resolveExecutorType(result.output()),
                resolveModelCode(context, result.output(), llmInvocationResult),
                resolveToolCode(result.output()),
                llmInvocationResult == null ? 0 : llmInvocationResult.inputTokenCount(),
                llmInvocationResult == null ? 0 : llmInvocationResult.outputTokenCount(),
                resolveTotalTokenCount(result.output(), llmInvocationResult),
                calculateEstimatedCost(resolveExecutorType(result.output()), result.output(), llmInvocationResult),
                Instant.now()
        );
        usageRecordRepository.save(record);
        log.info("记录用量摘要, traceId={}, capabilityCode={}, agentCode={}, executorType={}, toolCode={}, totalTokens={}, estimatedCost={}",
                record.traceId(),
                record.capabilityCode(),
                record.agentCode(),
                record.executorType(),
                record.toolCode(),
                record.totalTokenCount(),
                record.estimatedCostAmount());
        return record;
    }

    private BigDecimal calculateEstimatedCost(String executorType, Object output, LlmInvocationResult llmInvocationResult) {
        if (llmInvocationResult != null) {
            return usageProperties.getCost().getLlmTokenCostAmount()
                    .multiply(BigDecimal.valueOf(llmInvocationResult.totalTokenCount()));
        }
        int totalTokenCount = resolveTotalTokenCount(output, null);
        if (totalTokenCount > 0) {
            return usageProperties.getCost().getLlmTokenCostAmount()
                    .multiply(BigDecimal.valueOf(totalTokenCount));
        }
        if ("TOOL".equals(executorType)) {
            return usageProperties.getCost().getToolCallCostAmount();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 从输出对象中推断执行器类型。
     *
     * @param output 执行输出
     * @return 执行器类型
     */
    private String resolveExecutorType(Object output) {
        if (!(output instanceof Map<?, ?> outputMap)) {
            return "UNKNOWN";
        }
        Object invocationResult = outputMap.get("invocationResult");
        if (invocationResult instanceof ToolInvocationResult toolInvocationResult) {
            return toolInvocationResult.executorType();
        }
        if (invocationResult instanceof LlmInvocationResult llmInvocationResult) {
            return llmInvocationResult.executorType();
        }
        Object executorType = outputMap.get("executorType");
        if (executorType != null) {
            return String.valueOf(executorType);
        }
        Object toolResult = outputMap.get("toolResult");
        if (toolResult instanceof ToolInvocationResult toolInvocationResult) {
            return toolInvocationResult.executorType();
        }
        if (outputMap.containsKey("toolResult")) {
            return "TOOL";
        }
        Object llmResult = outputMap.get("llmResult");
        if (llmResult instanceof LlmInvocationResult llmInvocationResult) {
            return llmInvocationResult.executorType();
        }
        if (outputMap.containsKey("llmResult")) {
            return "LLM";
        }
        return "UNKNOWN";
    }

    /**
     * 从输出对象中提取 Tool 编码。
     *
     * @param output 执行输出
     * @return Tool 编码
     */
    private String resolveToolCode(Object output) {
        if (!(output instanceof Map<?, ?> outputMap)) {
            return null;
        }
        Object invocationResult = outputMap.get("invocationResult");
        if (invocationResult instanceof ToolInvocationResult toolInvocationResult) {
            return toolInvocationResult.toolCode();
        }
        Object toolResult = outputMap.get("toolResult");
        if (toolResult instanceof ToolInvocationResult toolInvocationResult) {
            return toolInvocationResult.toolCode();
        }
        if (!(toolResult instanceof Map<?, ?> toolMap)) {
            return null;
        }
        Object toolCode = toolMap.get("toolCode");
        return toolCode == null ? null : String.valueOf(toolCode);
    }

    /**
     * 从执行上下文与输出对象中提取模型编码。
     *
     * @param context              运行时上下文
     * @param output               执行输出
     * @param llmInvocationResult  LLM 调用结果
     * @return 模型编码
     */
    private String resolveModelCode(ExecutionContext context, Object output, LlmInvocationResult llmInvocationResult) {
        if (llmInvocationResult != null) {
            return llmInvocationResult.modelCode();
        }
        if (output instanceof Map<?, ?> outputMap && outputMap.get("modelCode") != null) {
            return String.valueOf(outputMap.get("modelCode"));
        }
        if (!"LLM".equals(resolveExecutorType(output))) {
            return null;
        }
        return context.agent().modelCode();
    }

    /**
     * 从执行输出中提取总 token 数，兼容 ReAct 输出结构。
     *
     * @param output              执行输出
     * @param llmInvocationResult LLM 调用结果
     * @return 总 token 数
     */
    private int resolveTotalTokenCount(Object output, LlmInvocationResult llmInvocationResult) {
        if (llmInvocationResult != null) {
            return llmInvocationResult.totalTokenCount();
        }
        if (!(output instanceof Map<?, ?> outputMap)) {
            return 0;
        }
        Object totalTokens = outputMap.get("totalTokens");
        if (totalTokens instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    /**
     * 从输出对象中提取 LLM 调用结果。
     *
     * @param output 执行输出
     * @return LLM 调用结果，非 LLM 分支时返回 null
     */
    private LlmInvocationResult resolveLlmInvocationResult(Object output) {
        if (!(output instanceof Map<?, ?> outputMap)) {
            return null;
        }
        Object invocationResult = outputMap.get("invocationResult");
        if (invocationResult instanceof LlmInvocationResult llmInvocationResult) {
            return llmInvocationResult;
        }
        Object llmResult = outputMap.get("llmResult");
        if (llmResult instanceof LlmInvocationResult llmInvocationResult) {
            return llmInvocationResult;
        }
        return null;
    }
}
