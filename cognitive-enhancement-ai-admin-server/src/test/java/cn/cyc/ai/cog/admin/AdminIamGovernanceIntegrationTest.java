package cn.cyc.ai.cog.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 账号治理域 API 冒烟（admin-it profile）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
class AdminIamGovernanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPermissionTree() throws Exception {
        mockMvc.perform(get("/api/admin/permissions/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldListTenants() throws Exception {
        mockMvc.perform(post("/api/admin/iam/tenants/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldListOrganizations() throws Exception {
        mockMvc.perform(post("/api/admin/orgs/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldReturnSecurityConfig() throws Exception {
        mockMvc.perform(post("/api/admin/system/security-configs/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }
}
