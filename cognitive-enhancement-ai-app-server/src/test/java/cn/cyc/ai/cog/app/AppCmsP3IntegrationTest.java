package cn.cyc.ai.cog.app;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("app-it")
class AppCmsP3IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldListAndMarkInAppMessagesRead() throws Exception {
        mockMvc.perform(post("/api/app/ops/in-app-messages/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].read", is(false)));

        mockMvc.perform(post("/api/app/ops/in-app-messages/read")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.read", is(true)));

        Integer readFlag = jdbcTemplate.queryForObject(
                "SELECT read_flag FROM qz_ops_in_app_message WHERE id = 1", Integer.class);
        org.junit.jupiter.api.Assertions.assertEquals(1, readFlag);
    }

    @Test
    void shouldCreateAndListSupportTickets() throws Exception {
        mockMvc.perform(post("/api/app/ops/support-tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "App 提单", "body": "需要人工协助", "category": "GENERAL"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("App 提单")))
                .andExpect(jsonPath("$.data.status", is("OPEN")));

        mockMvc.perform(post("/api/app/ops/support-tickets/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records[?(@.title == 'App 提单')]").exists());
    }
}
