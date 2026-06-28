package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;

import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Harness REST 控制器集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(RuntimeLlmTestDoubleConfiguration.class)
class HarnessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnScenarioTemplates() throws Exception {
        mockMvc.perform(get("/api/admin/harness/scenario-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].name", is("智能问答")));
    }

    @Test
    void shouldRunHarnessAndQueryReport() throws Exception {
        MvcResult runResult = mockMvc.perform(post("/api/admin/harness/run")
                        .header(TraceContextFilter.TRACE_ID_HEADER, "trace-harness-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "scenario": {
                                    "capabilityCode": "capability.qa.answer",
                                    "agentCode": "agent.qa",
                                    "skillCodes": ["skill.qa"],
                                    "toolCodes": ["tool.search"],
                                    "modelCode": "qwen-plus",
                                    "inputParams": {"question": "Harness 集成测试"}
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.harnessId", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("RUNNING")))
                .andReturn();

        String harnessId = JsonPath.read(runResult.getResponse().getContentAsString(), "$.data.harnessId");
        assertNotNull(harnessId);

        HarnessReportSnapshot report = waitForReport(harnessId, 30);
        assertTrue(report.finished());
        assertNotNull(report.status());

        mockMvc.perform(get("/api/admin/harness/reports/{harnessId}", harnessId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.harnessId", is(harnessId)))
                .andExpect(jsonPath("$.data.status", is(report.status())))
                .andExpect(jsonPath("$.data.steps.length()", org.hamcrest.Matchers.greaterThan(0)));

        mockMvc.perform(get("/api/admin/harness/reports")
                        .param("status", report.status())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].harnessId", is(harnessId)));
    }

    private HarnessReportSnapshot waitForReport(String harnessId, int maxAttempts) throws Exception {
        for (int i = 0; i < maxAttempts; i++) {
            MvcResult result = mockMvc.perform(get("/api/admin/harness/reports/{harnessId}", harnessId))
                    .andExpect(status().isOk())
                    .andReturn();
            String body = result.getResponse().getContentAsString();
            if (body.contains("\"status\":\"PASSED\"")
                    || body.contains("\"status\":\"FAILED\"")
                    || body.contains("\"status\":\"CANCELLED\"")
                    || body.contains("\"status\":\"PARTIAL\"")) {
                String status = extractJsonField(body, "status");
                return new HarnessReportSnapshot(status, true);
            }
            Thread.sleep(200);
        }
        return new HarnessReportSnapshot(null, false);
    }

    private String extractJsonField(String json, String field) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("\"" + field + "\"\\s*:\\s*\"([^\"]+)\"")
                .matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    private record HarnessReportSnapshot(String status, boolean finished) {
    }
}
