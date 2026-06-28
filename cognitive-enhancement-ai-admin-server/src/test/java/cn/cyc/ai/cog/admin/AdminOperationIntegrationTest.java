package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.platform.operations.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
class AdminOperationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AnnouncementService announcementService;

    @Test
    void shouldPageAnnouncements() throws Exception {
        mockMvc.perform(post("/api/admin/operations/announcements/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldPublishDueDraftAnnouncements() throws Exception {
        int published = announcementService.publishDueScheduled();
        assertEquals(1, published);

        mockMvc.perform(post("/api/admin/operations/announcements/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PUBLISHED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records[0].title", is("定时发布公告")))
                .andExpect(jsonPath("$.data.records[0].status", is("PUBLISHED")));
    }

    @Test
    void shouldPageMessageTemplates() throws Exception {
        mockMvc.perform(post("/api/admin/operations/message-templates/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldSendInAppMessageByTemplate() throws Exception {
        jdbcTemplate.update("DELETE FROM qz_ops_in_app_message WHERE user_id = 1");

        mockMvc.perform(post("/api/admin/operations/message-templates/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": 1, "recipient": "1", "params": {}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accepted", is(true)))
                .andExpect(jsonPath("$.data.channel", is("IN_APP")));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM qz_ops_in_app_message WHERE user_id = 1", Integer.class);
        org.junit.jupiter.api.Assertions.assertTrue(count != null && count >= 1);
    }
}
