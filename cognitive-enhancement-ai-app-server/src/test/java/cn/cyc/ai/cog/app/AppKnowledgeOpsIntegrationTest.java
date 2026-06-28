package cn.cyc.ai.cog.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * C 端知识库与学习链路集成测试（app-it profile）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("app-it")
class AppKnowledgeOpsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldListPublishedKnowledgeContents() throws Exception {
        mockMvc.perform(post("/api/app/knowledge/contents/page")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records[0].title", is("Phase3 测试文章")));
    }

    @Test
    void shouldReturnPublishedContentDetail() throws Exception {
        mockMvc.perform(get("/api/app/knowledge/contents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.body", is("正文内容")))
                .andExpect(jsonPath("$.data.locked", is(false)));
    }

    @Test
    void shouldListLearningModes() throws Exception {
        mockMvc.perform(get("/api/app/learning/modes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.modes").isArray());
    }

    @Test
    void shouldListActiveBanners() throws Exception {
        mockMvc.perform(post("/api/app/ops/banners/page")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"position": "HOME_TOP"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].title", is("首页 Banner")));
    }

    @Test
    void shouldListPublishedAnnouncements() throws Exception {
        mockMvc.perform(get("/api/app/ops/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[*].title", hasItem("系统公告")));
    }
}
