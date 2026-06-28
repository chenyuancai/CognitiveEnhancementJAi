package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import cn.cyc.ai.cog.runtime.usage.domain.UsageAccount;
import cn.cyc.ai.cog.runtime.usage.spi.UsageAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 用量额度账户集成测试。
 *
 * @author cyc
 */
@SpringBootTest(properties = {
        "cog.runtime.usage.account.enabled=true",
        "cog.runtime.usage.account.default-balance-amount=1.000000",
        "cog.runtime.usage.account.preflight-cost-amount=0.000010",
        "cog.runtime.usage.cost.llm-token-cost-amount=0.000001"
})
@AutoConfigureMockMvc
@Import(RuntimeLlmTestDoubleConfiguration.class)
class CapabilityRuntimeUsageAccountControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 账户仓储，用于构造测试初始余额。
     */
    @Autowired
    private UsageAccountRepository usageAccountRepository;

    /**
     * 验证租户额度不足时，能力执行入口返回 429。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldReturnTooManyRequestsWhenUsageAccountBalanceInsufficient() throws Exception {
        saveDefaultAccount("0.000001", "0.000000");

        executeCapability()
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0429")))
                .andExpect(jsonPath("$.message", is("租户额度余额不足: default")));
    }

    /**
     * 查询当前租户账户时自动创建默认账户。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldReturnCurrentUsageAccount() throws Exception {
        saveDefaultAccount("1.000000", "0.000000");

        mockMvc.perform(get("/api/runtime/usage/account"))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.tenantCode", is("default")))
                .andExpect(content().string(containsString("\"balanceAmount\":1.000000")))
                .andExpect(content().string(containsString("\"usedAmount\":0.000000")))
                .andExpect(jsonPath("$.data.enabled", is(true)));
    }

    /**
     * 执行成功后按实际 LLM token 成本扣减账户余额。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldDeductUsageAccountAfterSuccessfulExecution() throws Exception {
        saveDefaultAccount("1.000000", "0.000000");

        executeCapability().andExpect(status().isOk());

        mockMvc.perform(get("/api/runtime/usage/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balanceAmount", lessThan(1.0)))
                .andExpect(jsonPath("$.data.usedAmount", lessThan(1.0)))
                .andExpect(jsonPath("$.data.usedAmount").value(org.hamcrest.Matchers.greaterThan(0.0)));
    }

    private org.springframework.test.web.servlet.ResultActions executeCapability() throws Exception {
        return mockMvc.perform(post("/api/runtime/capabilities/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "capabilityCode": "capability.qa.answer",
                          "input": {
                            "question": "什么是 Cognitive Enhancement AI?"
                          }
                        }
                        """));
    }

    private void saveDefaultAccount(String balanceAmount, String usedAmount) {
        usageAccountRepository.save(new UsageAccount(
                "default",
                new BigDecimal(balanceAmount),
                new BigDecimal(usedAmount),
                true,
                Instant.now()
        ));
    }
}
