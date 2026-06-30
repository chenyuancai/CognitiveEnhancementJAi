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
 * @date 2026/6/15 14:18
 */
@Component
public class ObservationStatsAggregator {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(ObservationStatsAggregator.class);

    /** executionRecord仓储。 */
    private final ExecutionRecordRepository executionRecordRepository;
    /** usageRecord仓储。 */
    private final UsageRecordRepository usageRecordRepository;

    /**
     * 创建ObservationStats聚合器。
     */
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

    /**
     * 构建摘要。
     *
     * @param executions executions
     * @param usages usages
     * @return 构建结果
     */
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

    /**
     * 执行aggregate人能力。
     * @return 执行结果
     */
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

    /**
     * 执行aggregateUsage人键。
     * @return 执行结果
     */
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

    /**
     * 执行normalize键。
     *
     * @param key 键
     * @return 执行结果
     */
    private String normalizeKey(String key) {
        return key == null || key.isBlank() ? "UNKNOWN" : key;
    }

    /**
     * 执行能力Dimension键。
     *
     * @param capabilityCode 能力编码
     * @param capabilityVersion 能力版本号
     * @return 执行结果
     */
    private String capabilityDimensionKey(String capabilityCode, String capabilityVersion) {
        String code = normalizeKey(capabilityCode);
        if (capabilityVersion == null || capabilityVersion.isBlank()) {
            return code;
        }
        return code + "@" + capabilityVersion;
    }

    /**
     * 执行matches时间Range。
     *
     * @param recordedAt recordedAt
     * @param startTime start时间
     * @param endTime end时间
     * @return 执行结果
     */
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

    /**
     * MutableDimensionStats
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    private static final class MutableDimensionStats {
        /** invocation数量。 */
        private int invocationCount;
        /** 成功数量。 */
        private int successCount;
        /** failed数量。 */
        private int failedCount;
        /** 总数Tokens。 */
        private long totalTokens;
        /** 总数Cost。 */
        private BigDecimal totalCost = BigDecimal.ZERO;
        /** lastRecordedAt。 */
        private Instant lastRecordedAt;

        /**
         * 转换为uch。
         *
         * @param recordedAt recordedAt
         * @return 转换结果
         */
        private void touch(Instant recordedAt) {
            if (recordedAt == null) {
                return;
            }
            if (lastRecordedAt == null || recordedAt.isAfter(lastRecordedAt)) {
                lastRecordedAt = recordedAt;
            }
        }

        /**
         * 转换为Stats。
         *
         * @param dimensionKey dimension键
         * @return 转换结果
         */
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
