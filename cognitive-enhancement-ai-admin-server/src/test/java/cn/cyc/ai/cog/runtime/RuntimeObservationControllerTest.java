package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.core.metadata.model.ModelDefinition;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
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

import java.time.Instant;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 观测查询控制器集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(RuntimeLlmTestDoubleConfiguration.class)
class RuntimeObservationControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 模型定义仓储。
     */
    @Autowired
    private ModelDefinitionRepository modelDefinitionRepository;

    /**
     * 验证执行后可以查询执行记录和用量记录。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldQueryExecutionAndUsageRecordsAfterCapabilityExecution() throws Exception {
        String traceId = "trace-test-query-001";
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, traceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "观测查询是否可用?"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, traceId));

        mockMvc.perform(get("/api/runtime/observations/executions")
                        .param("traceId", traceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.traceId", notNullValue()))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].traceId", is(traceId)))
                .andExpect(jsonPath("$.data.items[0].capabilityCode", is("capability.qa.answer")))
                .andExpect(jsonPath("$.data.items[0].agentCode", is("agent.qa")))
                .andExpect(jsonPath("$.data.items[0].resultStatus", is("SUCCESS")))
                .andExpect(jsonPath("$.data.items[0].success", is(true)));

        mockMvc.perform(get("/api/runtime/observations/executions/{traceId}", traceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.execution.traceId", is(traceId)))
                .andExpect(jsonPath("$.data.execution.input.params.question", is("观测查询是否可用?")))
                .andExpect(jsonPath("$.data.execution.routing.agentCode", is("agent.qa")))
                .andExpect(jsonPath("$.data.execution.result.status", is("SUCCESS")))
                .andExpect(jsonPath("$.data.usages[0].executorType", is("REACT")))
                .andExpect(jsonPath("$.data.usages[0].modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.usages[0].totalTokenCount").value(org.hamcrest.Matchers.greaterThan(0)));

        mockMvc.perform(get("/api/runtime/observations/usages")
                        .param("traceId", traceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.traceId", notNullValue()))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].traceId", is(traceId)))
                .andExpect(jsonPath("$.data.items[0].capabilityCode", is("capability.qa.answer")))
                .andExpect(jsonPath("$.data.items[0].agentCode", is("agent.qa")))
                .andExpect(jsonPath("$.data.items[0].executorType", is("REACT")))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.items[0].totalTokenCount").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    /**
     * 验证观测查询支持按 traceId、capabilityCode、agentCode 过滤。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldFilterObservationRecordsByQueryParameters() throws Exception {
        modelDefinitionRepository.save(new ModelDefinition(
                "openai",
                "Mock OpenAI",
                "gpt-4o-mini",
                "GPT-4o mini",
                "chat",
                "https://mock.local/llm",
                "mock-secret",
                30000,
                1,
                CommonStatus.ENABLED,
                100,
                null
        ));

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, "trace-filter-tool-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "工具链路筛选"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, "trace-filter-llm-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.chat.generate",
                                  "input": {
                                    "question": "大模型链路筛选"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/runtime/observations/executions")
                        .param("traceId", "trace-filter-llm-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].traceId", is("trace-filter-llm-001")))
                .andExpect(jsonPath("$.data.items[0].capabilityCode", is("capability.chat.generate")))
                .andExpect(jsonPath("$.data.items[0].agentCode", is("agent.chat")));

        mockMvc.perform(get("/api/runtime/observations/usages")
                        .param("capabilityCode", "capability.chat.generate")
                        .param("agentCode", "agent.chat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].traceId", is("trace-filter-llm-001")))
                .andExpect(jsonPath("$.data.items[0].executorType", is("LLM")))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("gpt-4o-mini")))
                .andExpect(jsonPath("$.data.items[0].toolCode", nullValue()));
    }

    /**
     * 验证 capability 执行后可查询 Trace Span 与审计日志。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldQueryTraceSpansAndAuditLogsAfterCapabilityExecution() throws Exception {
        String traceId = "trace-test-span-audit-001";
        executeQaCapability(traceId, "Trace Span 是否可用?");

        mockMvc.perform(get("/api/runtime/observations/traces/{traceId}/spans", traceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.data.items[?(@.spanType == 'CAPABILITY')]").exists())
                .andExpect(jsonPath("$.data.items[?(@.spanType == 'AGENT')]").exists())
                .andExpect(jsonPath("$.data.items[?(@.spanType == 'LLM')]").exists());

        mockMvc.perform(get("/api/runtime/observations/audit-logs")
                        .param("traceId", traceId)
                        .param("eventType", "RUNTIME_INVOCATION")
                        .param("action", "EXECUTE_SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].traceId", is(traceId)))
                .andExpect(jsonPath("$.data.items[0].resourceCode", is("capability.qa.answer")))
                .andExpect(jsonPath("$.data.items[0].success", is(true)));
    }

    /**
     * 验证执行记录和用量记录支持分页与按记录时间排序。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldPageAndSortExecutionAndUsageRecords() throws Exception {
        executeQaCapability("trace-page-tool-001", "分页第一条");
        executeQaCapability("trace-page-tool-002", "分页第二条");

        mockMvc.perform(get("/api/runtime/observations/executions")
                        .param("page", "1")
                        .param("size", "1")
                        .param("sort", "recordedAt,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.page", is(1)))
                .andExpect(jsonPath("$.data.size", is(1)))
                .andExpect(jsonPath("$.data.totalPages", is(2)))
                .andExpect(jsonPath("$.data.hasNext", is(true)))
                .andExpect(jsonPath("$.data.items[0].traceId", is("trace-page-tool-001")));

        mockMvc.perform(get("/api/runtime/observations/usages")
                        .param("page", "2")
                        .param("size", "1")
                        .param("sort", "recordedAt,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.page", is(2)))
                .andExpect(jsonPath("$.data.size", is(1)))
                .andExpect(jsonPath("$.data.totalPages", is(2)))
                .andExpect(jsonPath("$.data.hasNext", is(false)))
                .andExpect(jsonPath("$.data.items[0].traceId", is("trace-page-tool-002")));
    }

    /**
     * 验证执行记录支持 startTime / endTime 时间窗口筛选。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldFilterExecutionRecordsByTimeWindow() throws Exception {
        executeQaCapability("trace-time-window-001", "时间窗口测试");
        Instant now = Instant.now();

        mockMvc.perform(get("/api/runtime/observations/executions")
                        .param("traceId", "trace-time-window-001")
                        .param("startTime", now.minusSeconds(120).toString())
                        .param("endTime", now.plusSeconds(120).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(1)));

        mockMvc.perform(get("/api/runtime/observations/executions")
                        .param("traceId", "trace-time-window-001")
                        .param("startTime", now.plusSeconds(3600).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(0)));
    }

    /**
     * 验证观测聚合统计接口。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldAggregateObservationStats() throws Exception {
        executeQaCapability("trace-stats-001", "聚合统计测试");

        mockMvc.perform(get("/api/runtime/observations/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.summary.totalExecutions").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.byCapability[0].dimensionKey", is("capability.qa.answer@1.0.0")))
                .andExpect(jsonPath("$.data.byModel[0].dimensionKey", is("gpt-4o-mini")));
    }

    /**
     * 验证模型检查记录支持分页与按记录时间排序。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldPageAndSortModelCheckRecords() throws Exception {
        checkQwenPlusModel("trace-page-model-001");
        checkQwenPlusModel("trace-page-model-002");

        mockMvc.perform(get("/api/runtime/observations/model-checks")
                        .param("providerCode", "bailian")
                        .param("modelCode", "qwen-plus")
                        .param("page", "1")
                        .param("size", "1")
                        .param("sort", "recordedAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(2)))
                .andExpect(jsonPath("$.data.page", is(1)))
                .andExpect(jsonPath("$.data.size", is(1)))
                .andExpect(jsonPath("$.data.totalPages", is(2)))
                .andExpect(jsonPath("$.data.hasNext", is(true)))
                .andExpect(jsonPath("$.data.items[0].traceId", is("trace-page-model-002")));
    }

    /**
     * 验证模型检查后可以查询模型检查记录。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldQueryModelCheckRecordsAfterConnectivityCheck() throws Exception {
        String traceId = "trace-model-check-001";
        checkQwenPlusModel(traceId);

        mockMvc.perform(get("/api/runtime/observations/model-checks")
                        .param("traceId", traceId)
                        .param("providerCode", "bailian")
                        .param("modelCode", "qwen-plus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.traceId", notNullValue()))
                .andExpect(jsonPath("$.data.total", is(1)))
                .andExpect(jsonPath("$.data.items[0].traceId", is(traceId)))
                .andExpect(jsonPath("$.data.items[0].providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.items[0].modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.items[0].reachable", is(true)))
                .andExpect(jsonPath("$.data.items[0].mock", is(false)))
                .andExpect(jsonPath("$.data.items[0].answerPreview", is("这是百炼返回的演示回答。")));

        mockMvc.perform(get("/api/runtime/observations/model-checks/latest")
                        .param("providerCode", "bailian")
                        .param("modelCode", "qwen-plus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.found", is(true)))
                .andExpect(jsonPath("$.data.item.traceId", is(traceId)))
                .andExpect(jsonPath("$.data.item.providerCode", is("bailian")))
                .andExpect(jsonPath("$.data.item.modelCode", is("qwen-plus")))
                .andExpect(jsonPath("$.data.item.reachable", is(true)));
    }

    /**
     * 执行 QA 能力。
     *
     * @param traceId  链路标识
     * @param question 用户问题
     * @throws Exception 测试异常
     */
    private void executeQaCapability(String traceId, String question) throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header(TraceContextFilter.TRACE_ID_HEADER, traceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "%s"
                                  }
                                }
                                """.formatted(question)))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, traceId));
    }

    /**
     * 检查 qwen-plus 模型连通性。
     *
     * @param traceId 链路标识
     * @throws Exception 测试异常
     */
    private void checkQwenPlusModel(String traceId) throws Exception {
        mockMvc.perform(post("/api/runtime/models/check")
                        .header(TraceContextFilter.TRACE_ID_HEADER, traceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "modelCode": "qwen-plus",
                                  "prompt": "请回复：模型检查成功。"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, traceId))
                .andExpect(jsonPath("$.data.reachable", is(true)));
    }
}
