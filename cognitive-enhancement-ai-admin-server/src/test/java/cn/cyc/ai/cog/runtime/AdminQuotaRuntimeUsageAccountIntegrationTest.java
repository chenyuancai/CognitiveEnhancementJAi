package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 扣 Token 与 Admin QuotaService 桥接集成测试。
 */
@SpringBootTest(properties = {
        "cog.runtime.usage.account.enabled=true",
        "cog.runtime.usage.account.backend=admin-quota",
        "cog.runtime.usage.account.preflight-cost-amount=0.000010",
        "cog.runtime.usage.cost.llm-token-cost-amount=0.000001"
})
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
@Import(RuntimeLlmTestDoubleConfiguration.class)
class AdminQuotaRuntimeUsageAccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDeductAdminQuotaAfterCapabilityExecution() throws Exception {
        mockMvc.perform(get("/api/admin/quota/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cycleRemaining", is("100000")));

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "什么是 Cognitive Enhancement AI?"
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER));

        mockMvc.perform(get("/api/admin/quota/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cycleRemaining", not("100000")));
    }

    @Test
    void shouldReturnTooManyRequestsWhenAdminQuotaInsufficient() throws Exception {
        mockMvc.perform(post("/api/admin/quota/accounts/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": 1,
                                  "bucket": "CYCLE",
                                  "deltaAmount": -100000,
                                  "remark": "test zero balance"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "额度不足测试"
                                  }
                                }
                                """))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0429")))
                .andExpect(jsonPath("$.message", is("账户 Token 额度不足")));
    }
}
