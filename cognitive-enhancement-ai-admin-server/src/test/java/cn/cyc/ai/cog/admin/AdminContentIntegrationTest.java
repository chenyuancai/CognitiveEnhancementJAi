package cn.cyc.ai.cog.admin;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@ActiveProfiles("admin-it")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminContentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldPageContents() throws Exception {
        mockMvc.perform(post("/api/admin/content/contents/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @Order(1)
    void shouldAuditPendingContentAndCreateVersionSnapshot() throws Exception {
        mockMvc.perform(post("/api/admin/content/contents/audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": 2, "pass": true, "remark": "审核通过"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PUBLISHED")))
                .andExpect(jsonPath("$.data.currentVersion", is(1)));

        mockMvc.perform(get("/api/admin/content/contents/2/versions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].versionNo", is(1)))
                .andExpect(jsonPath("$.data[0].title", is("待审核文章")));
    }

    @Test
    @Order(2)
    void shouldRollbackPublishedContentToDraft() throws Exception {
        mockMvc.perform(post("/api/admin/content/contents/rollback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": 2, "versionNo": 1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("DRAFT")))
                .andExpect(jsonPath("$.data.title", is("待审核文章")));
    }

    @Test
    void shouldListContentTags() throws Exception {
        mockMvc.perform(post("/api/admin/content/tags/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldPageKnowledgePackages() throws Exception {
        mockMvc.perform(post("/api/admin/content/knowledge-packages/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    void shouldPageImportJobs() throws Exception {
        mockMvc.perform(post("/api/admin/content/import-jobs/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.records").isArray());
    }
}
