package cn.cyc.ai.cog.runtime.observation.service;

import cn.cyc.ai.cog.runtime.observation.dto.ObservationDimensionStats;
import cn.cyc.ai.cog.runtime.observation.dto.ObservationStatsResult;
import cn.cyc.ai.cog.runtime.observation.dto.ObservationStatsSummary;
import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.observation.spi.ExecutionRecordRepository;
import cn.cyc.ai.cog.runtime.observation.spi.UsageRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 观测统计聚合器，按能力 / 模型 / Tool 维度汇总执行与用量数据。
 *
 * @author cyc
 */
@Component
public class ObservationStatsAggregator {

    private static final Logger log = LoggerFactory.getLogger(ObservationStatsAggregator.class);

    private final ExecutionRecordRepository executionRecordRepository;
    private final UsageRecordRepository usageRecordRepository;

    public ObservationStatsAggregator(ExecutionRecordRepository executionRecordRepository,
                                      UsageRecordRepository usageRecordRepository) {
        this.executionRecordRepository = executionRecordRepository;
        this.usageRecordRepository = usageRecordRepository;
    }

    /**
     * 聚合观测统计。
     *
     * @param startTime 起始时间（含）
     * @param endTime   结束时间（含）
     * @return 聚合统计结果
     */
    public ObservationStatsResult aggregate(Instant startTime, Instant endTime) {
        List<ExecutionRecord> executions = executionRecordRepository.listAll().stream()
                .filter(record -> matchesTimeRange(record.recordedAt(), startTime, endTime))
                .toList();
        List<UsageRecord> usages = usageRecordRepository.listAll().stream()
                .filter(record -> matchesTimeRange(record.recordedAt(), startTime, endTime))
                .toList();

        ObservationStatsSummary summary = buildSummary(executions, usages);
        List<ObservationDimensionStats> byCapability = aggregateByCapability(executions, usages);
        List<ObservationDimensionStats> byModel = aggregateUsageByKey(usages, UsageRecord::modelCode);
        List<ObservationDimensionStats> byTool = aggregateUsageByKey(usages, UsageRecord::toolCode);

        log.info("聚合观测统计, startTime={}, endTime={}, executions={}, usages={}, capabilities={}, models={}, tools={}",
                startTime, endTime, executions.size(), usages.size(),
                byCapability.size(), byModel.size(), byTool.size());

        return new ObservationStatsResult(startTime, endTime, summary, byCapability, byModel, byTool);
    }

    private ObservationStatsSummary buildSummary(List<ExecutionRecord> executions, List<UsageRecord> usages) {
        int successCount = (int) executions.stream().filter(ExecutionRecord::success).count();
        int failedCount = executions.size() - successCount;
        long totalTokens = usages.stream().mapToLong(UsageRecord::totalTokenCount).sum();
        BigDecimal totalCost = usages.stream()
                .map(UsageRecord::estimatedCostAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ObservationStatsSummary(
                executions.size(), successCount, failedCount, usages.size(), totalTokens, totalCost
        );
    }

    private List<ObservationDimensionStats> aggregateByCapability(List<ExecutionRecord> executions,
                                                                List<UsageRecord> usages) {
        Map<String, MutableDimensionStats> statsMap = new HashMap<>();

        for (ExecutionRecord execution : executions) {
            String key = capabilityDimensionKey(execution.capabilityCode(), execution.capabilityVersion());
            MutableDimensionStats stats = statsMap.computeIfAbsent(key, ignored -> new MutableDimensionStats());
            stats.invocationCount++;
            if (execution.success()) {
                stats.successCount++;
            } else {
                stats.failedCount++;
            }
            stats.touch(execution.recordedAt());
        }

        for (UsageRecord usage : usages) {
            String key = capabilityDimensionKey(usage.capabilityCode(), usage.capabilityVersion());
            MutableDimensionStats stats = statsMap.computeIfAbsent(key, ignored -> new MutableDimensionStats());
            if (stats.invocationCount == 0) {
                stats.invocationCount++;
            }
            stats.totalTokens += usage.totalTokenCount();
            if (usage.estimatedCostAmount() != null) {
                stats.totalCost = stats.totalCost.add(usage.estimatedCostAmount());
            }
            stats.touch(usage.recordedAt());
        }

        return statsMap.entrySet().stream()
                .map(entry -> entry.getValue().toStats(entry.getKey()))
                .sorted(Comparator.comparingInt(ObservationDimensionStats::invocationCount).reversed())
                .toList();
    }

    private List<ObservationDimensionStats> aggregateUsageByKey(List<UsageRecord> usages,
                                                              Function<UsageRecord, String> keyExtractor) {
        Map<String, MutableDimensionStats> statsMap = new HashMap<>();

        for (UsageRecord usage : usages) {
            String rawKey = keyExtractor.apply(usage);
            if (rawKey == null || rawKey.isBlank()) {
                continue;
            }
            String key = normalizeKey(rawKey);
            MutableDimensionStats stats = statsMap.computeIfAbsent(key, ignored -> new MutableDimensionStats());
            stats.invocationCount++;
            stats.totalTokens += usage.totalTokenCount();
            if (usage.estimatedCostAmount() != null) {
                stats.totalCost = stats.totalCost.add(usage.estimatedCostAmount());
            }
            stats.touch(usage.recordedAt());
        }

        return statsMap.entrySet().stream()
                .map(entry -> entry.getValue().toStats(entry.getKey()))
                .sorted(Comparator.comparingInt(ObservationDimensionStats::invocationCount).reversed())
                .toList();
    }

    private String normalizeKey(String key) {
        return key == null || key.isBlank() ? "UNKNOWN" : key;
    }

    private String capabilityDimensionKey(String capabilityCode, String capabilityVersion) {
        String code = normalizeKey(capabilityCode);
        if (capabilityVersion == null || capabilityVersion.isBlank()) {
            return code;
        }
        return code + "@" + capabilityVersion;
    }

    private boolean matchesTimeRange(Instant recordedAt, Instant startTime, Instant endTime) {
        if (startTime == null && endTime == null) {
            return true;
        }
        if (recordedAt == null) {
            return false;
        }
        if (startTime != null && recordedAt.isBefore(startTime)) {
            return false;
        }
        if (endTime != null && recordedAt.isAfter(endTime)) {
            return false;
        }
        return true;
    }

    private static final class MutableDimensionStats {
        private int invocationCount;
        private int successCount;
        private int failedCount;
        private long totalTokens;
        private BigDecimal totalCost = BigDecimal.ZERO;
        private Instant lastRecordedAt;

        private void touch(Instant recordedAt) {
            if (recordedAt == null) {
                return;
            }
            if (lastRecordedAt == null || recordedAt.isAfter(lastRecordedAt)) {
                lastRecordedAt = recordedAt;
            }
        }

        private ObservationDimensionStats toStats(String dimensionKey) {
            return new ObservationDimensionStats(
                    dimensionKey,
                    invocationCount,
                    successCount,
                    failedCount,
                    totalTokens,
                    totalCost,
                    lastRecordedAt
            );
        }
    }
}
