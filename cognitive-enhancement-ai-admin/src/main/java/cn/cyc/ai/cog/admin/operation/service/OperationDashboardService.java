package cn.cyc.ai.cog.admin.operation.service;

import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardQuery;
import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardResult;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.repository.IamUserRepository;
import cn.cyc.ai.cog.platform.knowledge.service.ContentImportJobService;
import cn.cyc.ai.cog.platform.knowledge.service.ContentService;
import cn.cyc.ai.cog.platform.membership.domain.AccountMembership;
import cn.cyc.ai.cog.platform.membership.repository.AccountMembershipRepository;
import cn.cyc.ai.cog.platform.quota.domain.TokenRecord;
import cn.cyc.ai.cog.platform.quota.repository.TokenRecordRepository;
import cn.cyc.ai.cog.common.context.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营看板聚合服务。
 */
@Service
public class OperationDashboardService {

    /** IAM 用户仓储 */
    private final IamUserRepository iamUserRepository;

    /** 账户会员关系仓储 */
    private final AccountMembershipRepository accountMembershipRepository;

    /** 订单仓储 */
    private final OrderRepository orderRepository;

    /** 内容服务 */
    private final ContentService contentService;

    /** 内容导入任务服务 */
    private final ContentImportJobService contentImportJobService;

    /** Token 流水仓储 */
    private final TokenRecordRepository tokenRecordRepository;

    /**
     * @param iamUserRepository             IAM 用户仓储
     * @param accountMembershipRepository   账户会员关系仓储
     * @param orderRepository               订单仓储
     * @param contentService                内容服务
     * @param contentImportJobService       内容导入任务服务
     * @param tokenRecordRepository         Token 流水仓储
     */
    public OperationDashboardService(IamUserRepository iamUserRepository,
                                     AccountMembershipRepository accountMembershipRepository,
                                     OrderRepository orderRepository,
                                     ContentService contentService,
                                     ContentImportJobService contentImportJobService,
                                     TokenRecordRepository tokenRecordRepository) {
        this.iamUserRepository = iamUserRepository;
        this.accountMembershipRepository = accountMembershipRepository;
        this.orderRepository = orderRepository;
        this.contentService = contentService;
        this.contentImportJobService = contentImportJobService;
        this.tokenRecordRepository = tokenRecordRepository;
    }

    /**
     * 构建运营看板聚合数据。
     *
     * @param query 时间范围与预设查询条件
     * @return 运营看板结果
     */
    public OperationDashboardResult build(OperationDashboardQuery query) {
        DateRange range = resolveRange(query);
        Long tenantId = TenantContext.currentTenantId();

        OperationDashboardResult result = new OperationDashboardResult();
        result.setPreset(StringUtils.hasText(query.getPreset()) ? query.getPreset() : "CUSTOM");
        result.setRangeStart(range.start());
        result.setRangeEnd(range.end());

        OperationDashboardResult.Summary summary = result.getSummary();
        summary.setTotalUsers(iamUserRepository.countUsers(tenantId, null, null));
        summary.setNewUsers(iamUserRepository.countUsers(tenantId, range.start(), range.end()));
        summary.setActiveMemberships(accountMembershipRepository.countByTenant(tenantId));
        summary.setTotalContentItems(contentService.countByTenant(tenantId));
        summary.setImportJobsInRange(contentImportJobService.countByTenantAndTimeRange(tenantId, range.start(), range.end()));
        summary.setGmvFen(orderRepository.sumPaidGmvFen(tenantId, range.start(), range.end()));
        summary.setTokenDeltaInRange(sumTokenDelta(tenantId, range.start(), range.end()));
        summary.setAiInvocationCount(tokenRecordRepository.countByTenantAndTypeAndTimeRange(
                tenantId, "DEDUCT", range.start(), range.end()));

        result.setUserGrowthTrend(buildUserGrowthTrend(tenantId, range));
        result.setMembershipLevelDistribution(buildMembershipDistribution(tenantId));
        result.setCapabilityInvocationDistribution(buildCapabilityDistribution(tenantId, range));
        result.setTokenCostTrend(buildTokenCostTrend(tenantId, range));
        return result;
    }

    private long sumTokenDelta(Long tenantId, LocalDateTime start, LocalDateTime end) {
        return tokenRecordRepository.listByTenantAndTimeRange(tenantId, start, end).stream()
                .mapToLong(record -> record.deltaAmount() == null ? 0L : Math.abs(record.deltaAmount()))
                .sum();
    }

    private List<OperationDashboardResult.DailyCount> buildUserGrowthTrend(Long tenantId, DateRange range) {
        Map<LocalDate, Long> dailyNew = new LinkedHashMap<>();
        LocalDate cursor = range.start().toLocalDate();
        LocalDate endDate = range.end().toLocalDate();
        while (!cursor.isAfter(endDate)) {
            dailyNew.put(cursor, 0L);
            cursor = cursor.plusDays(1);
        }
        for (IamUser user : iamUserRepository.listUsersCreatedBetween(tenantId, range.start(), range.end())) {
            if (user.createTime() == null) {
                continue;
            }
            LocalDate day = user.createTime().toLocalDate();
            dailyNew.computeIfPresent(day, (k, v) -> v + 1);
        }
        long cumulative = iamUserRepository.countUsers(tenantId, null, range.start().minusNanos(1));
        List<OperationDashboardResult.DailyCount> trend = new java.util.ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : dailyNew.entrySet()) {
            cumulative += entry.getValue();
            OperationDashboardResult.DailyCount item = new OperationDashboardResult.DailyCount();
            item.setDate(entry.getKey().toString());
            item.setNewUsers(entry.getValue());
            item.setCumulativeUsers(cumulative);
            trend.add(item);
        }
        return trend;
    }

    private List<OperationDashboardResult.LevelCount> buildMembershipDistribution(Long tenantId) {
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (AccountMembership membership : accountMembershipRepository.listByTenant(tenantId)) {
            String level = membership.levelCode() == null ? "UNKNOWN" : membership.levelCode();
            grouped.merge(level, 1L, Long::sum);
        }
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    OperationDashboardResult.LevelCount levelCount = new OperationDashboardResult.LevelCount();
                    levelCount.setLevelCode(entry.getKey());
                    levelCount.setCount(entry.getValue());
                    return levelCount;
                })
                .toList();
    }

    private List<OperationDashboardResult.CapabilityCount> buildCapabilityDistribution(Long tenantId,
                                                                                       DateRange range) {
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (TokenRecord record : tokenRecordRepository.listByTenantAndTimeRange(tenantId, range.start(), range.end())) {
            if (!"DEDUCT".equals(record.recordType())) {
                continue;
            }
            String code = StringUtils.hasText(record.bizType()) ? record.bizType() : "UNKNOWN";
            grouped.merge(code, 1L, Long::sum);
        }
        return grouped.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> {
                    OperationDashboardResult.CapabilityCount item = new OperationDashboardResult.CapabilityCount();
                    item.setCapabilityCode(entry.getKey());
                    item.setInvocationCount(entry.getValue());
                    return item;
                })
                .toList();
    }

    private List<OperationDashboardResult.DailyTokenCost> buildTokenCostTrend(Long tenantId, DateRange range) {
        Map<LocalDate, Long> daily = new LinkedHashMap<>();
        LocalDate cursor = range.start().toLocalDate();
        LocalDate endDate = range.end().toLocalDate();
        while (!cursor.isAfter(endDate)) {
            daily.put(cursor, 0L);
            cursor = cursor.plusDays(1);
        }
        for (TokenRecord record : tokenRecordRepository.listByTenantAndTimeRange(tenantId, range.start(), range.end())) {
            if (record.createTime() == null || record.deltaAmount() == null) {
                continue;
            }
            LocalDate day = record.createTime().toLocalDate();
            daily.computeIfPresent(day, (k, v) -> v + Math.abs(record.deltaAmount()));
        }
        return daily.entrySet().stream()
                .map(entry -> {
                    OperationDashboardResult.DailyTokenCost item = new OperationDashboardResult.DailyTokenCost();
                    item.setDate(entry.getKey().toString());
                    item.setTokenDelta(entry.getValue());
                    return item;
                })
                .toList();
    }

    private DateRange resolveRange(OperationDashboardQuery query) {
        LocalDateTime end = LocalDateTime.now();
        if (StringUtils.hasText(query.getPreset())) {
            return switch (query.getPreset()) {
                case "TODAY" -> new DateRange(LocalDate.now().atStartOfDay(), end);
                case "LAST_30_DAYS" -> new DateRange(end.minusDays(30), end);
                case "LAST_7_DAYS" -> new DateRange(end.minusDays(7), end);
                default -> new DateRange(end.minusDays(7), end);
            };
        }
        if (StringUtils.hasText(query.getStartTime()) && StringUtils.hasText(query.getEndTime())) {
            return new DateRange(LocalDateTime.parse(query.getStartTime()), LocalDateTime.parse(query.getEndTime()));
        }
        return new DateRange(end.minusDays(7), end);
    }

    private record DateRange(LocalDateTime start, LocalDateTime end) {
    }
}
