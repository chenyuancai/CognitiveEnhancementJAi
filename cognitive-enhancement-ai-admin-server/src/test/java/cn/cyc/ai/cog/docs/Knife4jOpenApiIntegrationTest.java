package cn.cyc.ai.cog.docs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Knife4j / OpenAPI 在线文档集成校验。
 */
@SpringBootTest
@AutoConfigureMockMvc
class Knife4jOpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeOpenApiDocsAndKnife4jUi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/runtime/capabilities/execute")))
                .andExpect(content().string(containsString("/api/center/agents")))
                .andExpect(content().string(containsString("/api/admin/auth/me")))
                .andExpect(content().string(containsString("/api/admin/ai/routing-overview")))
                .andExpect(content().string(containsString("/api/admin/ai/cost-dashboard")))
                .andExpect(content().string(containsString("同步执行能力")))
                .andExpect(content().string(containsString("Center - Agent")));

        mockMvc.perform(get("/doc.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Knife4j")));

        mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/v3/api-docs/runtime")))
                .andExpect(content().string(containsString("/v3/api-docs/center")))
                .andExpect(content().string(containsString("/v3/api-docs/admin")))
                .andExpect(content().string(containsString("/v3/api-docs/base")))
                .andExpect(content().string(containsString("/v3/api-docs/sse")));
    }
}
