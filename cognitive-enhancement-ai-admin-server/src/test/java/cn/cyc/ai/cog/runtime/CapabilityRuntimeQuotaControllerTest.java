package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import cn.cyc.ai.cog.runtime.support.RuntimeLlmTestDoubleConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 限流集成测试。
 *
 * @author cyc
 */
@SpringBootTest(properties = {
        "cog.runtime.quota.enabled=true",
        "cog.runtime.quota.application-limit-per-minute=1",
        "cog.runtime.quota.capability-limit-per-minute=0"
})
@AutoConfigureMockMvc
@Import(RuntimeLlmTestDoubleConfiguration.class)
class CapabilityRuntimeQuotaControllerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 验证应用级限流超限后返回 429。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldReturnTooManyRequestsWhenApplicationQuotaExceeded() throws Exception {
        executeCapability().andExpect(status().isOk());

        executeCapability()
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is("A0429")))
                .andExpect(jsonPath("$.message", is("应用级调用频率已超过限制: 1 次/分钟")));
    }

    /**
     * 执行测试能力。
     *
     * @return MockMvc 结果动作
     * @throws Exception 测试异常
     */
    private org.springframework.test.web.servlet.ResultActions executeCapability() throws Exception {
        return mockMvc.perform(post("/api/runtime/capabilities/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "capabilityCode": "capability.chat.generate",
                          "input": {
                            "question": "请用一句话介绍这个项目。"
                          }
                        }
                        """));
    }
}
