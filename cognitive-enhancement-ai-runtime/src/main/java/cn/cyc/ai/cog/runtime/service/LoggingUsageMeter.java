package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.api.LlmInvocationResult;
import cn.cyc.ai.cog.runtime.api.ToolInvocationResult;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.runtime.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.spi.UsageRecordRepository;
import cn.cyc.ai.cog.runtime.spi.UsageMeter;
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
     * 构造基于日志的默认用量记录器。
     *
     * @param usageRecordRepository 用量记录仓储
     */
    public LoggingUsageMeter(UsageRecordRepository usageRecordRepository) {
        this.usageRecordRepository = usageRecordRepository;
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
        UsageRecord record = new UsageRecord(
                context.traceId(),
                context.capability().capabilityCode(),
                context.agent().agentCode(),
                resolveExecutorType(result.output()),
                resolveModelCode(context, result.output()),
                resolveToolCode(result.output()),
                0,
                0,
                0,
                BigDecimal.ZERO,
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
     * @param context 运行时上下文
     * @param output  执行输出
     * @return 模型编码
     */
    private String resolveModelCode(ExecutionContext context, Object output) {
        if (!(output instanceof Map<?, ?> outputMap)) {
            return null;
        }
        Object invocationResult = outputMap.get("invocationResult");
        if (invocationResult instanceof LlmInvocationResult llmInvocationResult) {
            return llmInvocationResult.modelCode();
        }
        Object llmResult = outputMap.get("llmResult");
        if (llmResult instanceof LlmInvocationResult llmInvocationResult) {
            return llmInvocationResult.modelCode();
        }
        if (!"LLM".equals(resolveExecutorType(output))) {
            return null;
        }
        return context.agent().modelCode();
    }
}
