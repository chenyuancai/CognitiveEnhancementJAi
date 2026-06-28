package cn.cyc.ai.cog.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
class AdminSupportTicketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldPageSupportTickets() throws Exception {
        mockMvc.perform(post("/api/admin/operations/support-tickets/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records", hasSize(3)));
    }

    @Test
    void shouldCreateAndResolveSupportTicket() throws Exception {
        mockMvc.perform(post("/api/admin/operations/support-tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "IT 新建工单",
                                  "body": "集成测试创建",
                                  "category": "GENERAL",
                                  "priority": "NORMAL",
                                  "submitterUserId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("IT 新建工单")))
                .andExpect(jsonPath("$.data.status", is("OPEN")))
                .andExpect(jsonPath("$.data.ticketNo").exists());

        Long ticketId = jdbcTemplate.queryForObject(
                "SELECT id FROM qz_ops_support_ticket WHERE title = 'IT 新建工单' ORDER BY id DESC LIMIT 1",
                Long.class);

        mockMvc.perform(post("/api/admin/operations/support-tickets/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": %d,
                                  "status": "RESOLVED",
                                  "assigneeUserId": 1
                                }
                                """.formatted(ticketId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("RESOLVED")));

        Integer pendingCount = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM qz_ops_support_ticket
                        WHERE status IN ('OPEN', 'IN_PROGRESS') AND tenant_id = 1
                        """,
                Integer.class);
        org.junit.jupiter.api.Assertions.assertTrue(pendingCount != null && pendingCount >= 2);
    }

    @Test
    void shouldFilterOpenTickets() throws Exception {
        mockMvc.perform(post("/api/admin/operations/support-tickets/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"OPEN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records[0].status", is("OPEN")));
    }
}
