package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.ExecutionMode;
import cn.cyc.ai.cog.core.metadata.type.ParameterConstraintDefinition;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;
import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 高风险能力治理集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(RuntimeLlmTestDoubleConfiguration.class)
class CapabilityRuntimeRiskGovernanceControllerTest {

    /**
     * 高风险能力编码。
     */
    private static final String CAPABILITY_CODE = "capability.chat.high-risk";

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 能力定义仓储。
     */
    @Autowired
    private CapabilityDefinitionRepository capabilityDefinitionRepository;

    /**
     * 准备高风险能力定义。
     */
    @BeforeEach
    void setUp() {
        SchemaDefinition inputSchema = new SchemaDefinition(
                "object",
                "input",
                true,
                Map.of("question", new SchemaDefinition("string", "question", true, Map.of(), null, List.of())),
                null,
                List.of()
        );
        SchemaDefinition outputSchema = new SchemaDefinition(
                "object",
                "output",
                true,
                Map.of("answer", new SchemaDefinition("string", "answer", true, Map.of(), null, List.of())),
                null,
                List.of()
        );
        capabilityDefinitionRepository.save(new CapabilityDefinition(
                CAPABILITY_CODE,
                "高风险对话",
                "测试高风险对话能力",
                inputSchema,
                outputSchema,
                chatParameterConstraints(),
                ExecutionMode.SYNC,
                "agent.chat",
                RiskLevel.HIGH,
                true,
                CommonStatus.ENABLED
        ));
    }

    /**
     * 验证高风险能力未确认时被拒绝。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRejectHighRiskCapabilityWhenHumanConfirmMissing() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(false)))
                .andExpect(status().isForbidden())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0403")))
                .andExpect(jsonPath("$.message", is("策略检查未通过: 高风险能力需要人工确认")));
    }

    /**
     * 验证高风险能力确认后可执行且输出带审查标记。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldMarkConfirmedHighRiskOutputAsPendingReview() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.capability.capabilityCode", is(CAPABILITY_CODE)))
                .andExpect(jsonPath("$.data.result.output.governance.riskLevel", is("HIGH")))
                .andExpect(jsonPath("$.data.result.output.governance.reviewRequired", is(true)))
                .andExpect(jsonPath("$.data.result.output.governance.reviewStatus", is("PENDING_REVIEW")))
                .andExpect(jsonPath("$.data.result.output.governance.needHumanConfirm", is(true)));
    }

    /**
     * 构造请求体。
     *
     * @param humanConfirmed 是否人工确认
     * @return 请求 JSON
     */
    private String request(boolean humanConfirmed) {
        return """
                {
                  "capabilityCode": "%s",
                  "input": {
                    "question": "请用一句话介绍这个项目。"
                  },
                  "parameters": {
                    "humanConfirmed": %s
                  }
                }
                """.formatted(CAPABILITY_CODE, humanConfirmed);
    }

    /**
     * 构造对话能力参数约束。
     *
     * @return 参数约束
     */
    private Map<String, ParameterConstraintDefinition> chatParameterConstraints() {
        return Map.of(
                "temperature", new ParameterConstraintDefinition("number", false, 0D, 2D, false),
                "topP", new ParameterConstraintDefinition("number", false, 0.01D, 1D, false),
                "maxTokens", new ParameterConstraintDefinition("integer", false, 1D, 8192D, true)
        );
    }
}
