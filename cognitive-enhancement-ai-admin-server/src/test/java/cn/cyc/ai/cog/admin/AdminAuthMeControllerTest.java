package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Admin /me 集成测试（admin-it profile：H2 + 种子数据）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"admin-it"})
class AdminAuthMeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAuthMeWithPermissions() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.user.username", is("admin")))
                .andExpect(jsonPath("$.data.account.id", is("1")))
                .andExpect(jsonPath("$.data.permissions").isArray())
                .andExpect(jsonPath("$.data.permissions[?(@=='admin:order:refund')]").exists())
                .andExpect(jsonPath("$.data.membership.levelCode", is("FREE")))
                .andExpect(jsonPath("$.data.quota.cycleRemaining", notNullValue()));
    }
}
