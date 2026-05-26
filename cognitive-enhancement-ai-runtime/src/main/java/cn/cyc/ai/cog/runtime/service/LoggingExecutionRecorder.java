package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.api.ExecutionResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;
import cn.cyc.ai.cog.runtime.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.spi.ExecutionRecorder;
import cn.cyc.ai.cog.runtime.spi.ExecutionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 基于日志的默认执行记录器。
 *
 * @author cyc
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
                context.capability().capabilityCode(),
                context.agent().agentCode(),
                result.status(),
                true,
                Instant.now()
        );
        executionRecordRepository.save(record);
        log.info("记录执行摘要, traceId={}, capabilityCode={}, agentCode={}, status={}",
                record.traceId(), record.capabilityCode(), record.agentCode(), record.resultStatus());
        return record;
    }
}
