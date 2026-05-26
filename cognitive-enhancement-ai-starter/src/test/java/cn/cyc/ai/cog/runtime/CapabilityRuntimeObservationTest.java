package cn.cyc.ai.cog.runtime;

import cn.cyc.ai.cog.runtime.api.ExecutionResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionContext;
import cn.cyc.ai.cog.runtime.spi.ExecutionRecorder;
import cn.cyc.ai.cog.runtime.spi.UsageMeter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Runtime 观测占位集成测试。
 *
 * @author cyc
 */
@SpringBootTest
@AutoConfigureMockMvc
class CapabilityRuntimeObservationTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * 执行记录器 mock。
     */
    @MockBean
    private ExecutionRecorder executionRecorder;

    /**
     * 用量记录器 mock。
     */
    @MockBean
    private UsageMeter usageMeter;

    /**
     * 验证运行时链路会触发执行记录和用量记录。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldRecordExecutionAndUsageAfterCapabilityExecution() throws Exception {
        mockMvc.perform(post("/api/runtime/capabilities/execute")
                        .header("X-Trace-Id", "trace-test-observation-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "capabilityCode": "capability.qa.answer",
                                  "input": {
                                    "question": "观测占位是否生效?"
                                  }
                                }
                                """))
                .andExpect(status().isOk());

        verify(executionRecorder, times(1)).record(any(ExecutionContext.class), any(ExecutionResult.class));
        verify(usageMeter, times(1)).record(any(ExecutionContext.class), any(ExecutionResult.class));
    }
}
