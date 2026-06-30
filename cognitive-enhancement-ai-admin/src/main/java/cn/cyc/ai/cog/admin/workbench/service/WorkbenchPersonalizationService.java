package cn.cyc.ai.cog.admin.workbench.service;

import cn.cyc.ai.cog.admin.workbench.dto.DashboardOverview;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardTodo;
import cn.cyc.ai.cog.admin.workbench.dto.TrendSeries;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchMetricCard;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchQuickEntry;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchResult;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchTodoItem;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchQuery;
import cn.cyc.ai.cog.admin.workbench.support.WorkbenchPermissionChecker;
import cn.cyc.ai.cog.admin.workbench.support.WorkbenchRole;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.platform.common.dto.DailyPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 工作台 2A 角色化首页：按角色 + 子域权限组装待办、指标与快捷入口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class WorkbenchPersonalizationService {

    /** workbench服务。 */
    private final WorkbenchService workbenchService;

    /**
     * 创建WorkbenchPersonalization服务。
     *
     * @param workbenchService workbench服务
     */
    public WorkbenchPersonalizationService(WorkbenchService workbenchService) {
        this.workbenchService = workbenchService;
    }

    /**
     * 构建当前用户角色化工作台首页。
     */
    public WorkbenchResult personalized() {
        AuthUser user = UserContext.get();
        WorkbenchRole role = WorkbenchRole.resolve(user);
        DashboardOverview overview = workbenchService.overview();
        DashboardTodo todo = workbenchService.todo();
        List<TrendSeries> trends = workbenchService.trends(new WorkbenchQuery());

        WorkbenchResult result = new WorkbenchResult();
        result.setRole(role.name());
        result.setTodos(buildTodos(role, user, todo));
        result.setMetrics(buildMetrics(role, user, overview, trends));
        result.setQuickEntries(buildQuickEntries(role, user));
        return result;
    }

    /**
     * 构建Todos。
     *
     * @param role 角色
     * @param user 用户
     * @param todo todo
     * @return 构建结果
     */
    private List<WorkbenchTodoItem> buildTodos(WorkbenchRole role, AuthUser user, DashboardTodo todo) {
        List<WorkbenchTodoItem> items = new ArrayList<>();
        addTodo(items, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.CONTENT),
                "pendingAudit", "待审核内容", todo.getPendingAudit(), "/cms/content/review",
                "content:item:audit", "admin:content:audit");
        addTodo(items, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR, WorkbenchRole.SUPPORT),
                "pendingOrders", "待支付订单", todo.getPendingOrder(), "/cms/orders?status=PENDING",
                "billing:order:update", "admin:order:update");
        addTodo(items, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.CONTENT),
                "failedImport", "导入失败任务", todo.getFailedImport(), "/cms/content/import-jobs?status=FAILED",
                "content:item:update", "admin:content:update");
        addTodo(items, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "expiringMembers", "即将到期会员", todo.getExpiringMembers(), "/cms/members?filter=expiring",
                "membership:level:update", "admin:member:update");
        addTodo(items, role, user, EnumSet.of(WorkbenchRole.ADMIN),
                "aiAlerts", "AI 异常告警", todo.getAiAlerts(), "/cms/ai/routing",
                "ai:routing:read", "admin:ai:routing:read");
        addTodo(items, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.SUPPORT),
                "pendingTickets", "待处理工单", todo.getPendingTickets(), "/cms/support/tickets?status=OPEN",
                "ops:ticket:read", "admin:ticket:read");
        return items;
    }

    /**
     * 构建Metrics。
     * @return 构建结果
     */
    private List<WorkbenchMetricCard> buildMetrics(WorkbenchRole role, AuthUser user,
                                                   DashboardOverview overview, List<TrendSeries> trends) {
        List<WorkbenchMetricCard> metrics = new ArrayList<>();
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "userTodayNew", "今日新增用户", overview.getUserTodayNew(), "人",
                "iam:user:read", "admin:user:view");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "activeUsers", "近7日活跃用户", overview.getActiveUsers(), "人",
                "iam:user:read", "admin:user:view");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "gmvToday", "今日 GMV", overview.getRevenueToday(), "fen",
                "billing:order:update", "admin:order:update");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "paidMembers", "付费会员数", overview.getPaidMembers(), "人",
                "membership:level:update", "admin:member:update");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR, WorkbenchRole.SUPPORT),
                "orderPending", "待处理订单", overview.getOrderPending(), "单",
                "billing:order:update", "admin:order:update");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.CONTENT),
                "contentPublished7d", "近7日发布量", sumTrend(trends, "contentPublish"), "篇",
                "content:item:update", "admin:content:update");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.CONTENT),
                "contentPending", "待审核内容", overview.getContentPending(), "篇",
                "content:item:audit", "admin:content:audit");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN),
                "tokenCostToday", "今日 Token 消耗", overview.getAiTokenToday(), "token",
                "ai:cost:read", "admin:ai:cost:read");
        addMetric(metrics, role, user, EnumSet.of(WorkbenchRole.ADMIN),
                "userTotal", "用户总数", overview.getUserTotal(), "人",
                "iam:user:read", "admin:user:view");
        return metrics;
    }

    /**
     * 构建QuickEntries。
     *
     * @param role 角色
     * @param user 用户
     * @return 构建结果
     */
    private List<WorkbenchQuickEntry> buildQuickEntries(WorkbenchRole role, AuthUser user) {
        List<WorkbenchQuickEntry> entries = new ArrayList<>();
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR, WorkbenchRole.SUPPORT),
                "orders", "订单管理", "/cms/orders", "billing:order:update", "admin:order:update");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "members", "会员管理", "/cms/members", "membership:level:update", "admin:member:update");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "subscriptionPackages", "订阅套餐", "/cms/billing/subscription-packages",
                "billing:order:update", "admin:order:update");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.OPERATOR),
                "createBanner", "新建 Banner", "/cms/banners/new",
                "ops:banner:create", "admin:banner:create");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.CONTENT),
                "contents", "内容管理", "/cms/content/items",
                "content:item:update", "admin:content:update");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.CONTENT),
                "contentReview", "内容审核", "/cms/content/review",
                "content:item:audit", "admin:content:audit");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.CONTENT),
                "knowledgePackages", "知识包", "/cms/content/knowledge-packages",
                "content:item:update", "admin:content:update");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.SUPPORT),
                "users", "用户查询", "/cms/users",
                "iam:user:read", "admin:user:view");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN, WorkbenchRole.SUPPORT),
                "supportTickets", "客服工单", "/cms/support/tickets",
                "ops:ticket:read", "admin:ticket:read");
        addQuickEntry(entries, role, user, EnumSet.of(WorkbenchRole.ADMIN),
                "roles", "角色权限", "/cms/roles",
                "iam:role:update", "admin:role:update");
        return entries;
    }

    /**
     * 执行addTodo。
     */
    private void addTodo(List<WorkbenchTodoItem> items, WorkbenchRole role, AuthUser user,
                         Set<WorkbenchRole> allowedRoles, String key, String label,
                         long count, String link, String... permissions) {
        if (!allowedRoles.contains(role) || !WorkbenchPermissionChecker.hasAny(user, permissions)) {
            return;
        }
        WorkbenchTodoItem item = new WorkbenchTodoItem();
        item.setKey(key);
        item.setLabel(label);
        item.setCount(count);
        item.setLink(link);
        items.add(item);
    }

    /**
     * 执行addMetric。
     */
    private void addMetric(List<WorkbenchMetricCard> metrics, WorkbenchRole role, AuthUser user,
                           Set<WorkbenchRole> allowedRoles, String key, String label,
                           long value, String unit, String... permissions) {
        if (!allowedRoles.contains(role) || !WorkbenchPermissionChecker.hasAny(user, permissions)) {
            return;
        }
        WorkbenchMetricCard card = new WorkbenchMetricCard();
        card.setKey(key);
        card.setLabel(label);
        card.setValue(value);
        card.setUnit(unit);
        metrics.add(card);
    }

    /**
     * 执行addQuickEntry。
     */
    private void addQuickEntry(List<WorkbenchQuickEntry> entries, WorkbenchRole role, AuthUser user,
                               Set<WorkbenchRole> allowedRoles, String key, String label,
                               String link, String canonicalPermission, String aliasPermission) {
        if (!allowedRoles.contains(role)) {
            return;
        }
        if (!WorkbenchPermissionChecker.hasAny(user, canonicalPermission, aliasPermission)) {
            return;
        }
        WorkbenchQuickEntry entry = new WorkbenchQuickEntry();
        entry.setKey(key);
        entry.setLabel(label);
        entry.setLink(link);
        entry.setPermission(aliasPermission != null ? aliasPermission : canonicalPermission);
        entries.add(entry);
    }

    /**
     * 执行sumTrend。
     *
     * @param trends trends
     * @param metric metric
     * @return 执行结果
     */
    private long sumTrend(List<TrendSeries> trends, String metric) {
        if (trends == null) {
            return 0L;
        }
        return trends.stream()
                .filter(series -> metric.equals(series.getMetric()))
                .flatMap(series -> series.getPoints() == null ? java.util.stream.Stream.<DailyPoint>empty()
                        : series.getPoints().stream())
                .mapToLong(DailyPoint::value)
                .sum();
    }
}
