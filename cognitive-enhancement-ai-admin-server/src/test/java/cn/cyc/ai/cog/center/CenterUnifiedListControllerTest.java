package cn.cyc.ai.cog.center;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Center 统一列表查询集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
class CenterUnifiedListControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 验证六类 Center 资源均支持分页与资源专属过滤参数。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldPageAndFilterAllCenterResources() throws Exception {
        expectSingleItem("/api/center/models/page", "providerCode", "openai", "modelCode", "gpt-4o-mini");
        expectSingleItem("/api/center/prompts/page", "scenarioCode", "qa", "promptCode", "prompt.qa.default");
        mockMvc.perform(post("/api/center/tools/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "page": 1,
                                  "size": 10,
                                  "status": "ENABLED",
                                  "protocolType": "JAVA_LOCAL"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.items[?(@.toolCode=='tool.search')]").exists())
                .andExpect(jsonPath("$.data.items[?(@.toolCode=='tool.echo')]").exists());
        expectSingleItem("/api/center/skills/page", "skillType", "DOMAIN", "skillCode", "skill.qa");
        expectSingleItem("/api/center/agents/page", "modelCode", "qwen-plus", "agentCode", "agent.chat.bailian");
        expectSingleItem("/api/center/capabilities/page", "boundAgentCode", "agent.qa", "capabilityCode", "capability.qa.answer");
    }

    private void expectSingleItem(String path,
                                  String filterName,
                                  String filterValue,
                                  String codeField,
                                  String expectedCode) throws Exception {
        String body = """
                {
                  "page": 1,
                  "size": 10,
                  "status": "ENABLED",
                  "%s": "%s"
                }
                """.formatted(filterName, filterValue);
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.page", is(1)))
                .andExpect(jsonPath("$.data.size", is(10)))
                .andExpect(jsonPath("$.data.totalPages", is(1)))
                .andExpect(jsonPath("$.data.hasNext", is(false)))
                .andExpect(jsonPath("$.data.items[0]." + codeField, is(expectedCode)));
    }
}
