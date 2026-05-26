package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.api.LatestRuntimeRecordResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.spi.ExecutionRecordRepository;
import cn.cyc.ai.cog.runtime.spi.ModelCheckRecordRepository;
import cn.cyc.ai.cog.runtime.spi.UsageRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Runtime 观测查询服务。
 *
 * @author cyc
 */
@Service
public class RuntimeObservationQueryService {

    /**
     * 服务日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RuntimeObservationQueryService.class);

    /**
     * 执行记录仓储。
     */
    private final ExecutionRecordRepository executionRecordRepository;

    /**
     * 用量记录仓储。
     */
    private final UsageRecordRepository usageRecordRepository;

    /**
     * 模型检查记录仓储。
     */
    private final ModelCheckRecordRepository modelCheckRecordRepository;

    /**
     * 构造 Runtime 观测查询服务。
     *
     * @param executionRecordRepository 执行记录仓储
     * @param usageRecordRepository     用量记录仓储
     */
    public RuntimeObservationQueryService(ExecutionRecordRepository executionRecordRepository,
                                          UsageRecordRepository usageRecordRepository,
                                          ModelCheckRecordRepository modelCheckRecordRepository) {
        this.executionRecordRepository = executionRecordRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.modelCheckRecordRepository = modelCheckRecordRepository;
    }

    /**
     * 查询执行记录列表。
     *
     * @param traceId        链路标识筛选条件
     * @param capabilityCode 能力编码筛选条件
     * @param agentCode      Agent 编码筛选条件
     * @return 执行记录列表
     */
    public RuntimeListResult<ExecutionRecord> listExecutionRecords(String traceId,
                                                                   String capabilityCode,
                                                                   String agentCode,
                                                                   int page,
                                                                   int size,
                                                                   String sort) {
        List<ExecutionRecord> items = executionRecordRepository.listAll().stream()
                .filter(record -> matches(record.traceId(), traceId))
                .filter(record -> matches(record.capabilityCode(), capabilityCode))
                .filter(record -> matches(record.agentCode(), agentCode))
                .toList();
        RuntimeListResult<ExecutionRecord> result = pageByRecordedAt(items, page, size, sort, ExecutionRecord::recordedAt);
        log.info("查询执行记录列表, traceId={}, capabilityCode={}, agentCode={}, total={}, page={}, size={}, sort={}",
                traceId, capabilityCode, agentCode, items.size(), result.page(), result.size(), sort);
        return result;
    }

    /**
     * 查询用量记录列表。
     *
     * @param traceId        链路标识筛选条件
     * @param capabilityCode 能力编码筛选条件
     * @param agentCode      Agent 编码筛选条件
     * @return 用量记录列表
     */
    public RuntimeListResult<UsageRecord> listUsageRecords(String traceId,
                                                           String capabilityCode,
                                                           String agentCode,
                                                           int page,
                                                           int size,
                                                           String sort) {
        List<UsageRecord> items = usageRecordRepository.listAll().stream()
                .filter(record -> matches(record.traceId(), traceId))
                .filter(record -> matches(record.capabilityCode(), capabilityCode))
                .filter(record -> matches(record.agentCode(), agentCode))
                .toList();
        RuntimeListResult<UsageRecord> result = pageByRecordedAt(items, page, size, sort, UsageRecord::recordedAt);
        log.info("查询用量记录列表, traceId={}, capabilityCode={}, agentCode={}, total={}, page={}, size={}, sort={}",
                traceId, capabilityCode, agentCode, items.size(), result.page(), result.size(), sort);
        return result;
    }

    /**
     * 查询模型检查记录列表。
     *
     * @param traceId      链路标识筛选条件
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 模型检查记录列表
     */
    public RuntimeListResult<ModelCheckRecord> listModelCheckRecords(String traceId,
                                                                     String providerCode,
                                                                     String modelCode,
                                                                     int page,
                                                                     int size,
                                                                     String sort) {
        List<ModelCheckRecord> items = modelCheckRecordRepository.listAll().stream()
                .filter(record -> matches(record.traceId(), traceId))
                .filter(record -> matches(record.providerCode(), providerCode))
                .filter(record -> matches(record.modelCode(), modelCode))
                .toList();
        RuntimeListResult<ModelCheckRecord> result = pageByRecordedAt(items, page, size, sort, ModelCheckRecord::recordedAt);
        log.info("查询模型检查记录列表, traceId={}, providerCode={}, modelCode={}, total={}, page={}, size={}, sort={}",
                traceId, providerCode, modelCode, items.size(), result.page(), result.size(), sort);
        return result;
    }

    /**
     * 查询模型最近一次检查记录。
     *
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 最新检查记录
     */
    public LatestRuntimeRecordResult<ModelCheckRecord> getLatestModelCheckRecord(String providerCode, String modelCode) {
        ModelCheckRecord latestRecord = modelCheckRecordRepository.listAll().stream()
                .filter(record -> matches(record.providerCode(), providerCode))
                .filter(record -> matches(record.modelCode(), modelCode))
                .findFirst()
                .orElse(null);
        boolean found = latestRecord != null;
        log.info("查询模型最新检查记录, providerCode={}, modelCode={}, found={}", providerCode, modelCode, found);
        return new LatestRuntimeRecordResult<>(found, latestRecord);
    }

    /**
     * 判断记录字段是否命中筛选条件。
     *
     * @param actualRecordValue 实际记录值
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
     * 按记录时间排序并分页。
     *
     * @param records             原始记录
     * @param page                页码，从 1 开始
     * @param size                每页数量
     * @param sort                排序参数
     * @param recordedAtExtractor 记录时间提取器
     * @param <T>                 记录类型
     * @return 分页结果
     */
    private <T> RuntimeListResult<T> pageByRecordedAt(List<T> records,
                                                      int page,
                                                      int size,
                                                      String sort,
                                                      Function<T, Instant> recordedAtExtractor) {
        int normalizedPage = Math.max(page, 1);
        int normalizedSize = Math.max(1, Math.min(size, 100));
        Comparator<T> comparator = Comparator.comparing(
                recordedAtExtractor,
                Comparator.nullsLast(Comparator.naturalOrder())
        );
        if (!isAscendingRecordedAt(sort)) {
            comparator = comparator.reversed();
        }
        List<T> sortedRecords = records.stream()
                .sorted(comparator)
                .toList();
        int total = sortedRecords.size();
        int fromIndex = Math.min((normalizedPage - 1) * normalizedSize, total);
        int toIndex = Math.min(fromIndex + normalizedSize, total);
        List<T> pageItems = sortedRecords.subList(fromIndex, toIndex);
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / normalizedSize);
        boolean hasNext = normalizedPage < totalPages;
        return new RuntimeListResult<>(total, pageItems, normalizedPage, normalizedSize, totalPages, hasNext);
    }

    /**
     * 判断是否按记录时间升序排序。
     *
     * @param sort 排序参数
     * @return 是否升序
     */
    private boolean isAscendingRecordedAt(String sort) {
        if (sort == null || sort.isBlank()) {
            return false;
        }
        String normalizedSort = sort.trim().toLowerCase();
        return "recordedat,asc".equals(normalizedSort) || "recordedat:asc".equals(normalizedSort);
    }
}
