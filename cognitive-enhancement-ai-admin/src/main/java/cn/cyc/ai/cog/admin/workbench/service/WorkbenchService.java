package cn.cyc.ai.cog.admin.workbench.service;

import cn.cyc.ai.cog.admin.ai.dto.AiCostDashboardResult;
import cn.cyc.ai.cog.admin.ai.dto.AiRoutingOverviewResult;
import cn.cyc.ai.cog.admin.ai.service.AiRoutingOverviewService;
import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardQuery;
import cn.cyc.ai.cog.admin.operation.service.OperationDashboardService;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardOverview;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardResult;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardTodo;
import cn.cyc.ai.cog.admin.workbench.dto.DateRangeVO;
import cn.cyc.ai.cog.admin.workbench.dto.LevelCountItem;
import cn.cyc.ai.cog.admin.workbench.dto.TrendSeries;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchQuery;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.platform.billing.service.BillingStatsService;
import cn.cyc.ai.cog.platform.common.dto.DailyPoint;
import cn.cyc.ai.cog.platform.iam.service.AccountStatsService;
import cn.cyc.ai.cog.api.enums.ContentStatus;
import cn.cyc.ai.cog.platform.knowledge.service.ContentStatsService;
import cn.cyc.ai.cog.platform.membership.service.MembershipStatsService;
import cn.cyc.ai.cog.platform.operations.service.SupportTicketStatsService;
import cn.cyc.ai.cog.platform.quota.repository.TokenRecordRepository;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceStateResult;
import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerState;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作台聚合编排服务：概览、趋势、待办与 AI 看板内联。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class WorkbenchService {

    /** 缓存TTLMILLIS。 */
    private static final long CACHE_TTL_MILLIS = 60_000L;
    /** 默认TRENDDAYS。 */
    private static final int DEFAULT_TREND_DAYS = 7;
    /** ACTIVE用户DAYS。 */
    private static final int ACTIVE_USER_DAYS = 7;
    /** EXPIRINGMEMBERDAYS。 */
    private static final int EXPIRING_MEMBER_DAYS = 7;
    /** PUBLISHTRENDDAYS。 */
    private static final int PUBLISH_TREND_DAYS = 30;

    /** 看板结果缓存 */
    private final ConcurrentHashMap<String, CacheEntry> dashboardCache = new ConcurrentHashMap<>();

    /** 账号统计服务 */
    private final AccountStatsService accountStatsService;

    /** 会员统计服务 */
    private final MembershipStatsService membershipStatsService;

    /** 计费统计服务 */
    private final BillingStatsService billingStatsService;

    /** 内容统计服务 */
    private final ContentStatsService contentStatsService;

    /** Token 流水仓储 */
    private final TokenRecordRepository tokenRecordRepository;

    /** 运营看板服务（复用 AI 成本聚合） */
    private final OperationDashboardService operationDashboardService;

    /** AI 路由总览服务 */
    private final AiRoutingOverviewService aiRoutingOverviewService;

    /** 客服工单统计服务 */
    private final SupportTicketStatsService supportTicketStatsService;

    /**
     * @param accountStatsService         账号统计服务
     * @param membershipStatsService      会员统计服务
     * @param billingStatsService         计费统计服务
     * @param contentStatsService         内容统计服务
     * @param tokenRecordRepository       Token 流水仓储
     * @param operationDashboardService   运营看板服务
     * @param aiRoutingOverviewService    AI 路由总览服务
     * @param supportTicketStatsService   客服工单统计服务
     */
    public WorkbenchService(AccountStatsService accountStatsService,
                            MembershipStatsService membershipStatsService,
                            BillingStatsService billingStatsService,
                            ContentStatsService contentStatsService,
                            TokenRecordRepository tokenRecordRepository,
                            OperationDashboardService operationDashboardService,
                            AiRoutingOverviewService aiRoutingOverviewService,
                            SupportTicketStatsService supportTicketStatsService) {
        this.accountStatsService = accountStatsService;
        this.membershipStatsService = membershipStatsService;
        this.billingStatsService = billingStatsService;
        this.contentStatsService = contentStatsService;
        this.tokenRecordRepository = tokenRecordRepository;
        this.operationDashboardService = operationDashboardService;
        this.aiRoutingOverviewService = aiRoutingOverviewService;
        this.supportTicketStatsService = supportTicketStatsService;
    }

    /**
     * 一次性聚合工作台看板。
     *
     * @param query 查询参数
     * @return 看板聚合结果
     */
    public DashboardResult dashboard(WorkbenchQuery query) {
        DateRange range = resolveRange(query);
        String cacheKey = buildCacheKey(range);
        if (!Boolean.TRUE.equals(query.getRefresh())) {
            CacheEntry cached = dashboardCache.get(cacheKey);
            if (cached != null && !cached.isExpired()) {
                return cached.result();
            }
        }
        DashboardResult result = buildDashboard(range);
        dashboardCache.put(cacheKey, new CacheEntry(result, System.currentTimeMillis()));
        return result;
    }

    /**
     * 概览卡片。
     *
     * @return 概览数据
     */
    public DashboardOverview overview() {
        return buildOverview(resolveRange(new WorkbenchQuery()));
    }

    /**
     * 趋势曲线。
     *
     * @param query 日期区间
     * @return 趋势序列列表
     */
    public List<TrendSeries> trends(WorkbenchQuery query) {
        DateRange range = resolveRange(query);
        return buildTrends(range);
    }

    /**
     * 待办/告警。
     *
     * @return 待办汇总
     */
    public DashboardTodo todo() {
        return buildTodo(TenantContext.currentTenantId(), aiRoutingOverviewService.build());
    }

    /**
     * 构建Dashboard。
     *
     * @param range range
     * @return 构建结果
     */
    private DashboardResult buildDashboard(DateRange range) {
        Long tenantId = TenantContext.currentTenantId();
        AiRoutingOverviewResult aiRouting = aiRoutingOverviewService.build();
        OperationDashboardQuery aiQuery = new OperationDashboardQuery();
        aiQuery.setPreset("CUSTOM");
        aiQuery.setStartTime(range.start().atStartOfDay().toString());
        aiQuery.setEndTime(range.end().plusDays(1).atStartOfDay().minusNanos(1).toString());
        AiCostDashboardResult aiCost = AiCostDashboardResult.from(operationDashboardService.build(aiQuery));

        DashboardResult result = new DashboardResult();
        DateRangeVO rangeVo = new DateRangeVO();
        rangeVo.setFrom(range.start().toString());
        rangeVo.setTo(range.end().toString());
        result.setRange(rangeVo);
        result.setOverview(buildOverview(range));
        result.setTrends(buildTrends(range));
        result.setTodo(buildTodo(tenantId, aiRouting));
        result.setAiCost(aiCost);
        result.setAiRouting(aiRouting);
        return result;
    }

    /**
     * 构建Overview。
     *
     * @param range range
     * @return 构建结果
     */
    private DashboardOverview buildOverview(DateRange range) {
        Long tenantId = TenantContext.currentTenantId();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay().minusNanos(1);
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        DashboardOverview overview = new DashboardOverview();
        overview.setUserTotal(accountStatsService.countUsers(tenantId));
        overview.setUserTodayNew(accountStatsService.countUsersCreatedOn(tenantId, today));
        overview.setActiveUsers(accountStatsService.countActiveUsers(tenantId, ACTIVE_USER_DAYS));
        overview.setPaidMembers(membershipStatsService.countPaidMembers(tenantId));
        overview.setMemberLevelDist(toLevelItems(membershipStatsService.levelDistribution(tenantId)));
        overview.setRevenueToday(billingStatsService.revenueSum(tenantId, todayStart, todayEnd));
        overview.setRevenueMonth(billingStatsService.revenueSum(tenantId, monthStart, todayEnd));
        overview.setOrderTotal(billingStatsService.countAllOrders(tenantId));
        overview.setOrderPending(billingStatsService.countPendingOrders(tenantId));
        overview.setContentTotal(contentStatsService.countContents(tenantId));
        overview.setContentPending(contentStatsService.countByStatus(tenantId, ContentStatus.PENDING.code()));
        overview.setAiTokenToday(sumTokenDelta(tenantId, todayStart, todayEnd));
        overview.setAiCostToday(overview.getAiTokenToday());
        return overview;
    }

    /**
     * 构建Trends。
     *
     * @param range range
     * @return 构建结果
     */
    private List<TrendSeries> buildTrends(DateRange range) {
        Long tenantId = TenantContext.currentTenantId();
        List<TrendSeries> trends = new ArrayList<>();

        TrendSeries userGrowth = new TrendSeries();
        userGrowth.setMetric("userGrowth");
        userGrowth.setPoints(accountStatsService.userGrowth(tenantId, range.start(), range.end()));
        trends.add(userGrowth);

        TrendSeries revenue = new TrendSeries();
        revenue.setMetric("revenue");
        revenue.setPoints(billingStatsService.revenueTrend(tenantId, range.start(), range.end()));
        trends.add(revenue);

        TrendSeries aiCost = new TrendSeries();
        aiCost.setMetric("aiCost");
        aiCost.setPoints(buildAiCostTrend(tenantId, range));
        trends.add(aiCost);

        LocalDate publishFrom = range.end().minusDays(PUBLISH_TREND_DAYS - 1L);
        TrendSeries publish = new TrendSeries();
        publish.setMetric("contentPublish");
        publish.setPoints(contentStatsService.publishTrend(tenantId, publishFrom, range.end()));
        trends.add(publish);

        return trends;
    }

    /**
     * 构建Todo。
     *
     * @param tenantId 租户 ID
     * @param aiRouting aiRouting
     * @return 构建结果
     */
    private DashboardTodo buildTodo(Long tenantId, AiRoutingOverviewResult aiRouting) {
        DashboardTodo todo = new DashboardTodo();
        todo.setPendingAudit(contentStatsService.countByStatus(tenantId, ContentStatus.PENDING.code()));
        todo.setPendingOrder(billingStatsService.countPendingOrders(tenantId));
        todo.setFailedImport(contentStatsService.countFailedImports(tenantId));
        todo.setExpiringMembers(membershipStatsService.countExpiring(tenantId, EXPIRING_MEMBER_DAYS));
        todo.setAiAlerts(countAiAlerts(aiRouting));
        todo.setPendingTickets(supportTicketStatsService.countPending(tenantId));
        return todo;
    }

    /**
     * 构建AiCostTrend。
     *
     * @param tenantId 租户 ID
     * @param range range
     * @return 构建结果
     */
    private List<DailyPoint> buildAiCostTrend(Long tenantId, DateRange range) {
        List<DailyPoint> points = new ArrayList<>();
        LocalDate cursor = range.start();
        while (!cursor.isAfter(range.end())) {
            LocalDateTime dayStart = cursor.atStartOfDay();
            LocalDateTime dayEnd = cursor.plusDays(1).atStartOfDay().minusNanos(1);
            points.add(new DailyPoint(cursor.toString(), sumTokenDelta(tenantId, dayStart, dayEnd)));
            cursor = cursor.plusDays(1);
        }
        return points;
    }

    /**
     * 执行sum令牌Delta。
     *
     * @param tenantId 租户 ID
     * @param start start
     * @param end end
     * @return 执行结果
     */
    private long sumTokenDelta(Long tenantId, LocalDateTime start, LocalDateTime end) {
        return tokenRecordRepository.listByTenantAndTimeRange(tenantId, start, end).stream()
                .filter(record -> "DEDUCT".equals(record.recordType()))
                .mapToLong(record -> record.deltaAmount() == null ? 0L : Math.abs(record.deltaAmount()))
                .sum();
    }

    /**
     * 执行数量AiAlerts。
     *
     * @param aiRouting aiRouting
     * @return 执行结果
     */
    private long countAiAlerts(AiRoutingOverviewResult aiRouting) {
        if (aiRouting.getGovernanceStates() == null) {
            return 0;
        }
        return aiRouting.getGovernanceStates().stream()
                .map(ModelGovernanceStateResult::circuitState)
                .filter(state -> state == ModelCircuitBreakerState.OPEN)
                .count();
    }

    /**
     * 转换为等级Items。
     *
     * @param distribution distribution
     * @return 转换结果
     */
    private List<LevelCountItem> toLevelItems(Map<String, Long> distribution) {
        return distribution.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    LevelCountItem item = new LevelCountItem();
                    item.setLevel(entry.getKey());
                    item.setCount(entry.getValue());
                    return item;
                })
                .toList();
    }

    /**
     * 执行resolveRange。
     *
     * @param query 查询
     * @return 执行结果
     */
    private DateRange resolveRange(WorkbenchQuery query) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(DEFAULT_TREND_DAYS - 1L);
        if (query != null) {
            if (StringUtils.hasText(query.getTo())) {
                end = LocalDate.parse(query.getTo());
            }
            if (StringUtils.hasText(query.getFrom())) {
                start = LocalDate.parse(query.getFrom());
            }
        }
        if (start.isAfter(end)) {
            start = end;
        }
        return new DateRange(start, end);
    }

    /**
     * 构建缓存键。
     *
     * @param range range
     * @return 构建结果
     */
    private String buildCacheKey(DateRange range) {
        Long tenantId = TenantContext.currentTenantId();
        return tenantId + ":" + range.start() + ":" + range.end();
    }

    /**
     * DateRange 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    private record DateRange(LocalDate start, LocalDate end) {
    }

    /**
     * CacheEntry 记录
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    private record CacheEntry(DashboardResult result, long cachedAtMillis) {

        boolean isExpired() {
            return System.currentTimeMillis() - cachedAtMillis > CACHE_TTL_MILLIS;
        }
    }
}
