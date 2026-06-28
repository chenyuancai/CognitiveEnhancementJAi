package cn.cyc.ai.cog.admin.workbench.service;

import cn.cyc.ai.cog.admin.workbench.dto.DashboardOverview;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardTodo;
import cn.cyc.ai.cog.admin.workbench.dto.TrendSeries;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchResult;
import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.context.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkbenchPersonalizationServiceTest {

    @Mock
    private WorkbenchService workbenchService;

    @InjectMocks
    private WorkbenchPersonalizationService personalizationService;

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void shouldReturnAdminCardsForAdminRole() {
        bindUser(List.of("ADMIN"), List.of("admin:user:view", "admin:order:update", "admin:content:audit",
                "admin:member:update", "admin:ai:cost:read", "admin:ai:routing:read"));
        stubStats(3, 2, 1, 4, 5, 0);

        WorkbenchResult result = personalizationService.personalized();

        assertEquals("ADMIN", result.getRole());
        assertFalse(result.getTodos().isEmpty());
        assertTrue(result.getTodos().stream().anyMatch(item -> "aiAlerts".equals(item.getKey())));
        assertTrue(result.getMetrics().stream().anyMatch(card -> "tokenCostToday".equals(card.getKey())));
    }

    @Test
    void shouldReturnContentCardsForContentRole() {
        bindUser(List.of("CONTENT"), List.of("admin:content:update", "admin:content:audit"));
        stubStats(5, 0, 2, 0, 0, 0);

        WorkbenchResult result = personalizationService.personalized();

        assertEquals("CONTENT", result.getRole());
        assertTrue(result.getTodos().stream().anyMatch(item -> "pendingAudit".equals(item.getKey())));
        assertFalse(result.getTodos().stream().anyMatch(item -> "aiAlerts".equals(item.getKey())));
        assertTrue(result.getMetrics().stream().anyMatch(card -> "contentPending".equals(card.getKey())));
        assertFalse(result.getMetrics().stream().anyMatch(card -> "tokenCostToday".equals(card.getKey())));
        assertTrue(result.getQuickEntries().stream().anyMatch(entry -> "contentReview".equals(entry.getKey())));
    }

    @Test
    void shouldFilterQuickEntriesByPermission() {
        bindUser(List.of("OPERATOR"), List.of("admin:order:update"));
        stubStats(0, 1, 0, 0, 0, 0);

        WorkbenchResult result = personalizationService.personalized();

        assertEquals("OPERATOR", result.getRole());
        assertTrue(result.getQuickEntries().stream().anyMatch(entry -> "orders".equals(entry.getKey())));
        assertFalse(result.getQuickEntries().stream().anyMatch(entry -> "createBanner".equals(entry.getKey())));
    }

    @Test
    void shouldReturnSupportTicketsForSupportRole() {
        bindUser(List.of("SUPPORT"), List.of("admin:ticket:read", "admin:order:update"));
        stubStats(0, 1, 0, 0, 0, 2);

        WorkbenchResult result = personalizationService.personalized();

        assertEquals("SUPPORT", result.getRole());
        assertTrue(result.getTodos().stream().anyMatch(item -> "pendingTickets".equals(item.getKey())));
        assertTrue(result.getQuickEntries().stream().anyMatch(entry -> "supportTickets".equals(entry.getKey())));
        assertFalse(result.getTodos().stream().anyMatch(item -> "aiAlerts".equals(item.getKey())));
    }

    private void bindUser(List<String> roles, List<String> authorities) {
        AuthUser user = new AuthUser(1L, "tester", "platform", roles, authorities);
        user.setTenantId(1L);
        UserContext.set(user);
    }

    private void stubStats(long pendingAudit, long pendingOrder, long failedImport,
                           long expiringMembers, long aiAlerts, long pendingTickets) {
        DashboardOverview overview = new DashboardOverview();
        overview.setUserTotal(100);
        overview.setUserTodayNew(5);
        overview.setActiveUsers(40);
        overview.setPaidMembers(20);
        overview.setRevenueToday(10000);
        overview.setOrderPending(pendingOrder);
        overview.setContentPending(pendingAudit);
        overview.setAiTokenToday(5000);

        DashboardTodo todo = new DashboardTodo();
        todo.setPendingAudit(pendingAudit);
        todo.setPendingOrder(pendingOrder);
        todo.setFailedImport(failedImport);
        todo.setExpiringMembers(expiringMembers);
        todo.setAiAlerts(aiAlerts);
        todo.setPendingTickets(pendingTickets);

        when(workbenchService.overview()).thenReturn(overview);
        when(workbenchService.todo()).thenReturn(todo);
        when(workbenchService.trends(any())).thenReturn(List.of());
    }
}
