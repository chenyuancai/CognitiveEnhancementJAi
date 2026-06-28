package cn.cyc.ai.cog.runtime.smoke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static cn.cyc.ai.cog.support.SmokeTestTags.SMOKE;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 百炼模型检查真实联通 smoke 测试。
 *
 * <p>默认 {@code ./mvnw test} 不会执行（Surefire 排除 {@code smoke} 组）。
 * 手工验真：{@code COG_SMOKE_API_KEY=sk-xxx ./mvnw -Psmoke -pl cognitive-enhancement-ai-admin-server test}
 * <p>启动前通过 CMS 提供商接口写入 bailian 的 apiKey，不再读取 {@code DASHSCOPE_API_KEY} 环境变量。
 *
 * @author cyc
 */
@Tag(SMOKE)
@EnabledIfEnvironmentVariable(named = "COG_SMOKE_API_KEY", matches = ".+")
@SpringBootTest
@AutoConfigureMockMvc
class BailianModelConnectivitySmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void seedProviderApiKeyFromEnvironment() throws Exception {
        String apiKey = System.getenv("COG_SMOKE_API_KEY");
        mockMvc.perform(post("/api/center/model-providers/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "providerCode": "bailian",
                                  "providerName": "阿里云百炼",
                                  "providerType": "DASHSCOPE",
                                  "defaultEndpoint": "https://dashscope.aliyuncs.com/compatible-mode/v1",
                                  "apiKey": "%s",
                                  "description": "smoke test provider",
                                  "status": "ENABLED"
                                }
                                """.formatted(apiKey)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.apiKeyConfigured", is(true)));
    }

    @Test
    void shouldCheckQwenPlusWithRealApiKey() throws Exception {
        mockMvc.perform(post("/api/runtime/models/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "qwen-plus",
                                  "prompt": "请回复：smoke check ok"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reachable", is(true)))
                .andExpect(jsonPath("$.data.providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.mock", is(false)))
                .andExpect(jsonPath("$.data.answerPreview", notNullValue()))
                .andExpect(jsonPath("$.data.latencyMs", notNullValue()));
    }

    @Test
    void shouldRefreshQwenPlusStatusWithRealApiKey() throws Exception {
        mockMvc.perform(post("/api/runtime/models/statuses/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "qwen-plus",
                                  "prompt": "请回复：smoke refresh ok"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].reachable", is(true)))
                .andExpect(jsonPath("$.data.items[0].mock", is(false)));

        mockMvc.perform(get("/api/runtime/models/statuses")
                        .param("providerCode", "bailian")
                        .param("modelCode", "qwen-plus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].hasCheckRecord", is(true)))
                .andExpect(jsonPath("$.data.items[0].reachable", is(true)))
                .andExpect(jsonPath("$.data.items[0].mock", is(false)))
                .andExpect(jsonPath("$.data.items[0].healthStatus", is("REACHABLE")));
    }
}
