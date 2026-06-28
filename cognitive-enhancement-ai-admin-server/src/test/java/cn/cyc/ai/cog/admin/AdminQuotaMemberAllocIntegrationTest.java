package cn.cyc.ai.cog.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
class AdminQuotaMemberAllocIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldAllocateAndRemoveMemberQuota() throws Exception {
        jdbcTemplate.update("DELETE FROM qz_mbr_quota_member_alloc WHERE account_id = 1 AND user_id = 1");

        mockMvc.perform(post("/api/admin/quota/accounts/1/member-allocs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId": 1, "allocatedAmount": 5000}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.allocatedAmount", is("5000")));

        mockMvc.perform(get("/api/admin/quota/accounts/1/member-allocs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId", is("1")))
                .andExpect(jsonPath("$.data[0].allocatedAmount", is("5000")));

        mockMvc.perform(delete("/api/admin/quota/accounts/1/member-allocs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(get("/api/admin/quota/accounts/1/member-allocs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
