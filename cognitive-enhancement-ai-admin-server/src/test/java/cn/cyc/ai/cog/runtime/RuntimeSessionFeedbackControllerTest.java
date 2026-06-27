package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 会话与反馈集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(RuntimeLlmTestDoubleConfiguration.class)
class RuntimeSessionFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRecordSessionMessagesAfterCapabilityExecution() throws Exception {
        String createResponse = mockMvc.perform(post("/api/runtime/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "1",
                                  "capabilityCode": "capability.qa.answer",
                                  "title": "PH3 会话测试"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.sessionId").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String sessionId = JsonPath.read(createResponse, "$.data.sessionId");

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, "trace-session-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "会话上下文是否记录?"
                                  },
                                  "parameters": {
                                    "sessionId": "%s"
                                  }
                                }
                                """.formatted(sessionId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/runtime/sessions/{sessionId}/messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.items[0].role", is("USER")))
                .andExpect(jsonPath("$.data.items[1].role", is("ASSISTANT")));
    }

    @Test
    void shouldSubmitAndQueryExecutionFeedback() throws Exception {
        mockMvc.perform(post("/api/runtime/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traceId": "trace-feedback-001",
                                  "rating": 4,
                                  "originalAnswer": "原始回答",
                                  "correctedAnswer": "修正回答",
                                  "comment": "答案不够准确"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.feedbackId").isNotEmpty());

        mockMvc.perform(get("/api/runtime/feedback")
                        .param("traceId", "trace-feedback-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].traceId", is("trace-feedback-001")))
                .andExpect(jsonPath("$.data.items[0].correctedAnswer", is("修正回答")));
    }
}
