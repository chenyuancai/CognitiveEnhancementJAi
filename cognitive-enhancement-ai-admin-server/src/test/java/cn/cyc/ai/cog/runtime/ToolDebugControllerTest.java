package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.spi.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tool 调试调用集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
class ToolDebugControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 审计日志仓储。
     */
    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 验证单 Tool 调试调用不依赖 Skill 绑定，且返回 ToolRuntime 结果与耗时。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldDebugInvokeLocalToolAndReturnInvocationResult() throws Exception {
        mockMvc.perform(post("/api/runtime/tools/debug-invoke")
                        .header("X-Trace-Id", "trace-tool-debug-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toolCode": "tool.search",
                                  "input": {
                                    "question": "Tool 调试是否可用?"
                                  },
                                  "parameters": {
                                    "temperature": 0.2
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, "trace-tool-debug-001"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.traceId", is("trace-tool-debug-001")))
                .andExpect(jsonPath("$.data.toolCode", is("tool.search")))
                .andExpect(jsonPath("$.data.protocolType", is("JAVA_LOCAL")))
                .andExpect(jsonPath("$.data.riskLevel", is("LOW")))
                .andExpect(jsonPath("$.data.latencyMs", notNullValue()))
                .andExpect(jsonPath("$.data.invocationResult.executorType", is("TOOL")))
                .andExpect(jsonPath("$.data.invocationResult.toolCode", is("tool.search")))
                .andExpect(jsonPath("$.data.invocationResult.parameters.temperature", is(0.2)))
                .andExpect(jsonPath("$.data.invocationResult.toolPayload.handler", is("demoSearchTool")));
    }

    /**
     * 验证 Tool 调试调用会校验输入 Schema。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectDebugInvokeWhenInputSchemaIsInvalid() throws Exception {
        mockMvc.perform(post("/api/runtime/tools/debug-invoke")
                        .header("X-Trace-Id", "trace-tool-debug-invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toolCode": "tool.search",
                                  "input": {
                                    "question": 123
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0400")))
                .andExpect(jsonPath("$.message", is("Tool 输入参数 question 必须是 string")));
    }

    /**
     * 验证 HIGH 风险 Tool 调试前必须显式确认。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRequireConfirmationForHighRiskDebugInvoke() throws Exception {
        createHighRiskTool("tool.debug.high");

        mockMvc.perform(post("/api/runtime/tools/debug-invoke")
                        .header("X-Trace-Id", "trace-tool-debug-high")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toolCode": "tool.debug.high",
                                  "input": {
                                    "question": "高风险调试是否需要确认?"
                                  }
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0409")))
                .andExpect(jsonPath("$.message", is("HIGH 风险 Tool 调试需要 debugConfirmed=true")));
    }

    /**
     * 验证 Tool 调试调用会写入专用审计事件。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRecordAuditAfterDebugInvoke() throws Exception {
        mockMvc.perform(post("/api/runtime/tools/debug-invoke")
                        .header("X-Trace-Id", "trace-tool-debug-audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toolCode": "tool.search",
                                  "input": {
                                    "question": "调试审计是否记录?"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        boolean auditRecorded = auditLogRepository.listAll().stream()
                .anyMatch(this::isExpectedDebugAudit);
        assertTrue(auditRecorded);
    }

    private void createHighRiskTool(String toolCode) throws Exception {
        mockMvc.perform(post("/api/center/tools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toolCode": "%s",
                                  "toolName": "高风险调试工具",
                                  "protocolType": "JAVA_LOCAL",
                                  "requestSchema": {
                                    "type": "object",
                                    "description": "工具输入",
                                    "required": true,
                                    "properties": {
                                      "question": {
                                        "type": "string",
                                        "description": "用户问题",
                                        "required": true,
                                        "properties": {},
                                        "items": null,
                                        "enumValues": []
                                      }
                                    },
                                    "items": null,
                                    "enumValues": []
                                  },
                                  "responseSchema": {
                                    "type": "object",
                                    "description": "工具输出",
                                    "required": true,
                                    "properties": {},
                                    "items": null,
                                    "enumValues": []
                                  },
                                  "permissionScope": "debug:high",
                                  "riskLevel": "HIGH",
                                  "timeoutMs": 5000,
                                  "retryMaxAttempts": 1,
                                  "implRef": "demoSearchTool",
                                  "status": "ENABLED"
                                }
                                """.formatted(toolCode)))
                .andExpect(status().isOk());
    }

    private boolean isExpectedDebugAudit(AuditLogRecord record) {
        return "trace-tool-debug-audit".equals(record.traceId())
                && "TOOL_DEBUG_INVOKE".equals(record.eventType())
                && "DEBUG_INVOKE".equals(record.action())
                && "tool.search".equals(record.resourceCode())
                && record.success()
                && record.detail().containsKey("latencyMs");
    }
}
