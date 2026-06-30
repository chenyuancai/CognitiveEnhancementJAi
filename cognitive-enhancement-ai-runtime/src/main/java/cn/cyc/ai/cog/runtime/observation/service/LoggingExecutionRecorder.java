package cn.cyc.ai.cog.runtime.observation.service;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinition;
import cn.cyc.ai.cog.core.runtime.ExecutionContext;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.spi.ExecutionRecorder;
import cn.cyc.ai.cog.runtime.observation.spi.ExecutionRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * 基于日志的默认执行记录器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class LoggingExecutionRecorder implements ExecutionRecorder {

    /**
     * 执行记录日志。
     */
    private static final Logger log = LoggerFactory.getLogger(LoggingExecutionRecorder.class);

    /**
     * 执行记录仓储。
     */
    private final ExecutionRecordRepository executionRecordRepository;

    /**
     * 构造基于日志的默认执行记录器。
     *
     * @param executionRecordRepository 执行记录仓储
     */
    public LoggingExecutionRecorder(ExecutionRecordRepository executionRecordRepository) {
        this.executionRecordRepository = executionRecordRepository;
    }

    /**
     * 记录执行摘要并输出日志。
     *
     * @param context 运行时上下文
     * @param result  执行结果
     * @return 执行记录
     */
    @Override
    public ExecutionRecord record(ExecutionContext context, ExecutionResult result) {
        ExecutionRecord record = new ExecutionRecord(
                context.traceId(),
                TenantContext.currentTenantCode(),
                context.capability().capabilityCode(),
                context.capability().version(),
                context.agent().agentCode(),
                result.status(),
                true,
                null,
                Instant.now(),
                buildInput(context),
                buildRouting(context),
                buildResult(result)
        );
        executionRecordRepository.save(record);
        log.info("记录执行摘要, traceId={}, capabilityCode={}, agentCode={}, status={}",
                record.traceId(), record.capabilityCode(), record.agentCode(), record.resultStatus());
        return record;
    }

    /**
     * 记录一次能力执行失败摘要并输出日志。
     *
     * @param context       运行时上下文
     * @param failureReason 执行失败原因
     * @return 执行记录
     */
    @Override
    public ExecutionRecord recordFailure(ExecutionContext context, String failureReason) {
        ExecutionRecord record = new ExecutionRecord(
                context.traceId(),
                TenantContext.currentTenantCode(),
                context.capability() == null ? null : context.capability().capabilityCode(),
                context.capability() == null ? null : context.capability().version(),
                context.agent() == null ? null : context.agent().agentCode(),
                "FAILED",
                false,
                failureReason,
                Instant.now(),
                buildInput(context),
                buildRouting(context),
                null
        );
        executionRecordRepository.save(record);
        log.warn("记录执行失败摘要, traceId={}, capabilityCode={}, agentCode={}, failureReason={}",
                record.traceId(), record.capabilityCode(), record.agentCode(), record.failureReason());
        return record;
    }

    /**
     * 构建输入。
     *
     * @param context 上下文
     * @return 构建结果
     */
    private ExecutionRecord.ExecutionInputDetail buildInput(ExecutionContext context) {
        if (context.request() == null) {
            return null;
        }
        return new ExecutionRecord.ExecutionInputDetail(
                context.request().input(),
                context.request().parameters()
        );
    }

    /**
     * 构建Routing。
     *
     * @param context 上下文
     * @return 构建结果
     */
    private ExecutionRecord.ExecutionRoutingDetail buildRouting(ExecutionContext context) {
        CapabilityDefinition capability = context.capability();
        AgentDefinition agent = context.agent();
        List<String> skillCodes = context.skills() == null
                ? List.of()
                : context.skills().stream().map(SkillDefinition::skillCode).toList();
        return new ExecutionRecord.ExecutionRoutingDetail(
                capability == null ? null : capability.capabilityCode(),
                capability == null ? null : capability.capabilityName(),
                agent == null ? null : agent.agentCode(),
                agent == null ? null : agent.agentName(),
                context.prompt() == null ? null : context.prompt().promptCode(),
                skillCodes,
                agent == null ? null : agent.modelCode()
        );
    }

    /**
     * 构建结果。
     *
     * @param result 结果
     * @return 构建结果
     */
    private ExecutionRecord.ExecutionResultDetail buildResult(ExecutionResult result) {
        if (result == null) {
            return null;
        }
        return new ExecutionRecord.ExecutionResultDetail(
                result.status(),
                result.message(),
                result.allowedSkillCodes(),
                result.output()
        );
    }
}
