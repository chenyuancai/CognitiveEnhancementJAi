package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.runtime.api.ModelFailureSummaryResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusOverviewResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusSummaryResult;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.spi.ModelCheckRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Runtime 模型查询服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class ModelRuntimeQueryService {

    /**
     * 服务日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ModelRuntimeQueryService.class);

    /**
     * 模型定义仓储。
     */
    private final ModelDefinitionRepository modelDefinitionRepository;

    /**
     * 模型检查记录仓储。
     */
    private final ModelCheckRecordRepository modelCheckRecordRepository;

    /**
     * 构造 Runtime 模型查询服务。
     *
     * @param modelDefinitionRepository 模型定义仓储
     * @param modelCheckRecordRepository 模型检查记录仓储
     */
    public ModelRuntimeQueryService(ModelDefinitionRepository modelDefinitionRepository,
                                    ModelCheckRecordRepository modelCheckRecordRepository) {
        this.modelDefinitionRepository = modelDefinitionRepository;
        this.modelCheckRecordRepository = modelCheckRecordRepository;
    }

    /**
     * 查询模型状态摘要列表。
     *
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 模型状态摘要列表
     */
    public RuntimeListResult<ModelStatusSummaryResult> listModelStatuses(String providerCode, String modelCode) {
        List<ModelCheckRecord> records = modelCheckRecordRepository.listAll();
        Map<String, ModelCheckRecord> latestChecks = resolveLatestChecks(records);
        List<ModelStatusSummaryResult> items = modelDefinitionRepository.listAll().stream()
                .filter(model -> matches(model.providerCode(), providerCode))
                .filter(model -> matches(model.modelCode(), modelCode))
                .sorted(Comparator.comparingInt(ModelDefinition::routePriority).reversed()
                        .thenComparing(ModelDefinition::providerCode)
                        .thenComparing(ModelDefinition::modelCode))
                .map(model -> toSummaryResult(model,
                        latestChecks.get(buildModelKey(model.providerCode(), model.modelCode())),
                        records))
                .toList();
        log.info("查询模型状态摘要列表, providerCode={}, modelCode={}, total={}", providerCode, modelCode, items.size());
        return new RuntimeListResult<>(items.size(), items);
    }

    /**
     * 查询模型状态总览。
     *
     * @return 模型状态总览
     */
    public ModelStatusOverviewResult getModelStatusOverview() {
        List<ModelDefinition> definitions = modelDefinitionRepository.listAll();
        List<ModelCheckRecord> records = modelCheckRecordRepository.listAll();
        Map<String, ModelCheckRecord> latestChecks = resolveLatestChecks(records);

        int totalModels = definitions.size();
        int enabledModels = (int) definitions.stream().filter(model -> model.status() == CommonStatus.ENABLED).count();
        int disabledModels = totalModels - enabledModels;
        int checkedModels = (int) definitions.stream()
                .map(model -> latestChecks.get(buildModelKey(model.providerCode(), model.modelCode())))
                .filter(Objects::nonNull)
                .count();
        int reachableModels = (int) definitions.stream()
                .map(model -> latestChecks.get(buildModelKey(model.providerCode(), model.modelCode())))
                .filter(Objects::nonNull)
                .filter(ModelCheckRecord::reachable)
                .count();
        int unreachableModels = (int) definitions.stream()
                .map(model -> latestChecks.get(buildModelKey(model.providerCode(), model.modelCode())))
                .filter(Objects::nonNull)
                .filter(record -> !record.reachable())
                .count();
        int uncheckedModels = totalModels - checkedModels;

        Instant latestCheckedAt = records.stream()
                .map(ModelCheckRecord::recordedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
        Instant latestSuccessAt = records.stream()
                .filter(ModelCheckRecord::reachable)
                .map(ModelCheckRecord::recordedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
        Instant latestFailureAt = records.stream()
                .filter(record -> !record.reachable())
                .map(ModelCheckRecord::recordedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
        List<ModelFailureSummaryResult> failureSummaries = buildFailureSummaries(latestChecks);

        log.info("查询模型状态总览, totalModels={}, enabledModels={}, checkedModels={}, reachableModels={}, unreachableModels={}",
                totalModels, enabledModels, checkedModels, reachableModels, unreachableModels);
        return new ModelStatusOverviewResult(
                totalModels,
                enabledModels,
                disabledModels,
                checkedModels,
                reachableModels,
                unreachableModels,
                uncheckedModels,
                latestCheckedAt,
                latestSuccessAt,
                latestFailureAt,
                failureSummaries
        );
    }

    /**
     * 归并最近一次模型检查记录。
     *
     * @param records 模型检查记录列表
     * @return 最近检查记录映射
     */
    private Map<String, ModelCheckRecord> resolveLatestChecks(List<ModelCheckRecord> records) {
        Map<String, ModelCheckRecord> latestChecks = new HashMap<>();
        for (ModelCheckRecord record : records) {
            latestChecks.putIfAbsent(buildModelKey(record.providerCode(), record.modelCode()), record);
        }
        return latestChecks;
    }

    /**
     * 构建失败原因聚合结果。
     *
     * @param latestChecks 最近检查记录映射
     * @return 失败原因聚合列表
     */
    private List<ModelFailureSummaryResult> buildFailureSummaries(Map<String, ModelCheckRecord> latestChecks) {
        return latestChecks.values().stream()
                .filter(record -> !record.reachable())
                .filter(record -> record.failureReason() != null && !record.failureReason().isBlank())
                .collect(Collectors.groupingBy(ModelCheckRecord::failureReason))
                .entrySet().stream()
                .map(entry -> {
                    List<ModelCheckRecord> groupedRecords = entry.getValue();
                    Instant latestOccurredAt = groupedRecords.stream()
                            .map(ModelCheckRecord::recordedAt)
                            .max(Comparator.naturalOrder())
                            .orElse(null);
                    List<String> affectedModelCodes = groupedRecords.stream()
                            .map(ModelCheckRecord::modelCode)
                            .distinct()
                            .sorted()
                            .toList();
                    return new ModelFailureSummaryResult(
                            entry.getKey(),
                            groupedRecords.size(),
                            latestOccurredAt,
                            affectedModelCodes
                    );
                })
                .sorted(Comparator.comparing(ModelFailureSummaryResult::count).reversed()
                        .thenComparing(ModelFailureSummaryResult::reason))
                .toList();
    }

    /**
     * 转换模型状态摘要结果。
     *
     * @param definition 模型定义
     * @param latestCheck 最近一次检查记录
     * @param allRecords  全部模型检查记录
     * @return 模型状态摘要
     */
    private ModelStatusSummaryResult toSummaryResult(ModelDefinition definition,
                                                     ModelCheckRecord latestCheck,
                                                     List<ModelCheckRecord> allRecords) {
        boolean hasCheckRecord = latestCheck != null;
        String modelKey = buildModelKey(definition.providerCode(), definition.modelCode());
        List<ModelCheckRecord> modelRecords = allRecords.stream()
                .filter(record -> buildModelKey(record.providerCode(), record.modelCode()).equals(modelKey))
                .toList();
        Instant lastSuccessAt = modelRecords.stream()
                .filter(ModelCheckRecord::reachable)
                .map(ModelCheckRecord::recordedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
        Instant lastFailureAt = modelRecords.stream()
                .filter(record -> !record.reachable())
                .map(ModelCheckRecord::recordedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);
        int consecutiveFailureCount = calculateConsecutiveFailureCount(modelRecords);
        return new ModelStatusSummaryResult(
                definition.providerCode(),
                definition.providerName(),
                definition.modelCode(),
                definition.modelName(),
                definition.modelType(),
                definition.status().name(),
                resolveHealthStatus(definition, latestCheck),
                definition.routePriority(),
                definition.fallbackModelCode(),
                hasCheckRecord,
                hasCheckRecord ? latestCheck.reachable() : null,
                hasCheckRecord ? latestCheck.latencyMs() : null,
                hasCheckRecord ? latestCheck.mock() : null,
                hasCheckRecord ? latestCheck.failureReason() : null,
                hasCheckRecord ? latestCheck.answerPreview() : null,
                hasCheckRecord ? latestCheck.recordedAt() : null,
                lastSuccessAt,
                lastFailureAt,
                consecutiveFailureCount
        );
    }

    /**
     * 解析模型健康状态。
     *
     * @param definition 模型定义
     * @param latestCheck 最近检查记录
     * @return 健康状态
     */
    private String resolveHealthStatus(ModelDefinition definition, ModelCheckRecord latestCheck) {
        if (definition.status() == CommonStatus.DISABLED) {
            return "DISABLED";
        }
        if (latestCheck == null) {
            return "UNCHECKED";
        }
        return latestCheck.reachable() ? "REACHABLE" : "UNREACHABLE";
    }

    /**
     * 计算模型连续失败次数。
     *
     * @param modelRecords 模型检查记录
     * @return 连续失败次数
     */
    private int calculateConsecutiveFailureCount(List<ModelCheckRecord> modelRecords) {
        int count = 0;
        for (ModelCheckRecord record : modelRecords) {
            if (record.reachable()) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * 判断模型字段是否命中筛选条件。
     *
     * @param actualRecordValue 实际值
     * @param expectedFilter    期望筛选值
     * @return 是否命中
     */
    private boolean matches(String actualRecordValue, String expectedFilter) {
        if (expectedFilter == null || expectedFilter.isBlank()) {
            return true;
        }
        return Objects.equals(actualRecordValue, expectedFilter);
    }

    /**
     * 构造模型映射键。
     *
     * @param providerCode 模型提供方编码
     * @param modelCode    模型编码
     * @return 映射键
     */
    private String buildModelKey(String providerCode, String modelCode) {
        return providerCode + "#" + modelCode;
    }
}
