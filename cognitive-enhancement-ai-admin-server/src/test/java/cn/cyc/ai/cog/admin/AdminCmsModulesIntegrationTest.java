package cn.cyc.ai.cog.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CMS 核心模块 API 冒烟集成测试（admin-it profile）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
class AdminCmsModulesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListUsers() throws Exception {
        mockMvc.perform(post("/api/admin/users/page")
                        .contentType(APPLICATION_JSON)
                        .content("{\"current\":1,\"size\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].username", is("admin")));
    }

    @Test
    void shouldPageRoles() throws Exception {
        mockMvc.perform(post("/api/admin/roles/page")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldReturnOperationDashboard() throws Exception {
        mockMvc.perform(post("/api/admin/operations/dashboard/query")
                        .contentType(APPLICATION_JSON)
                        .content("{\"preset\":\"LAST_7_DAYS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.preset", is("LAST_7_DAYS")))
                .andExpect(jsonPath("$.data.summary.totalUsers").exists())
                .andExpect(jsonPath("$.data.userGrowthTrend").isArray())
                .andExpect(jsonPath("$.data.membershipLevelDistribution").isArray())
                .andExpect(jsonPath("$.data.capabilityInvocationDistribution").isArray())
                .andExpect(jsonPath("$.data.tokenCostTrend").isArray());
    }

    @Test
    void shouldReturnPersonalizedWorkbenchHome() throws Exception {
        mockMvc.perform(get("/api/admin/workbench"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.role", is("ADMIN")))
                .andExpect(jsonPath("$.data.todos").isArray())
                .andExpect(jsonPath("$.data.metrics").isArray())
                .andExpect(jsonPath("$.data.quickEntries").isArray());
    }

    @Test
    void shouldReturnWorkbenchDashboard() throws Exception {
        mockMvc.perform(post("/api/admin/workbench/dashboard/query")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.range.from").exists())
                .andExpect(jsonPath("$.data.overview.userTotal").exists())
                .andExpect(jsonPath("$.data.trends").isArray())
                .andExpect(jsonPath("$.data.todo").exists())
                .andExpect(jsonPath("$.data.aiCost").exists())
                .andExpect(jsonPath("$.data.aiRouting").exists());
    }

    @Test
    void shouldReturnAiCostDashboard() throws Exception {
        mockMvc.perform(post("/api/admin/ai/cost-dashboard/query")
                        .contentType(APPLICATION_JSON)
                        .content("{\"preset\":\"LAST_7_DAYS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.tokenCostTrend").isArray())
                .andExpect(jsonPath("$.data.capabilityInvocationDistribution").isArray());
    }

    @Test
    void shouldReturnAiRoutingOverview() throws Exception {
        mockMvc.perform(get("/api/admin/ai/routing-overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.modelOverview").exists())
                .andExpect(jsonPath("$.data.governanceStates").isArray())
                .andExpect(jsonPath("$.data.capabilityRoutes").isArray());
    }

    @Test
    void shouldListFinancialRecords() throws Exception {
        mockMvc.perform(post("/api/admin/billing/financial-records/page")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldListTokenRecords() throws Exception {
        mockMvc.perform(post("/api/admin/quota/token-records/page")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldListMembershipChangeLogs() throws Exception {
        mockMvc.perform(post("/api/admin/membership/levels/change-logs/page")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldListBanners() throws Exception {
        mockMvc.perform(post("/api/admin/operations/banners/page")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }
}
