package cn.cyc.ai.cog.infra.web;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 全局异常处理测试。
 *
 * @author cyc
 */
@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(controllers = ExceptionTestController.class)
@Import({GlobalExceptionHandler.class, TraceWebConfiguration.class, GlobalExceptionHandlerTest.TestTraceConfiguration.class})
class GlobalExceptionHandlerTest {

    /**
     * MockMvc 测试入口。
     */
    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    /**
     * 每个测试后清理线程上下文。
     */
    @AfterEach
    void tearDown() {
        TraceContext.clear();
    }

    /**
     * 验证业务异常会被转换为标准错误响应。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldConvertBusinessExceptionToStandardResponse() throws Exception {
        mockMvc.perform(get("/test/business").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, "trace-test-001"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is(ErrorCode.BAD_REQUEST.getCode())))
                .andExpect(jsonPath("$.message", is("参数不合法")))
                .andExpect(jsonPath("$.traceId", is("trace-test-001")));
    }

    /**
     * 验证未捕获异常会转换为系统错误响应。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldConvertUnhandledExceptionToStandardResponse() throws Exception {
        mockMvc.perform(get("/test/system").header(TraceContextFilter.TRACE_ID_HEADER, "trace-from-header"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, "trace-from-header"))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is(ErrorCode.SYSTEM_ERROR.getCode())))
                .andExpect(jsonPath("$.message", is(ErrorCode.SYSTEM_ERROR.getMessage())))
                .andExpect(jsonPath("$.traceId", is("trace-from-header")));
    }

    /**
     * 验证 TraceId 可在控制器内部读取。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldExposeTraceIdInsideController() throws Exception {
        mockMvc.perform(get("/test/trace"))
                .andExpect(status().isOk())
                .andExpect(header().string(TraceContextFilter.TRACE_ID_HEADER, "trace-test-001"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is("trace-test-001")))
                .andExpect(jsonPath("$.traceId", is("trace-test-001")));
    }

    /**
     * 验证缺失请求参数会转为 bad request。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldConvertMissingRequestParamToBadRequest() throws Exception {
        mockMvc.perform(get("/test/required-param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is(ErrorCode.BAD_REQUEST.getCode())))
                .andExpect(jsonPath("$.traceId", is("trace-test-001")));
    }

    /**
     * 验证参数类型不匹配会转为 bad request。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldConvertTypeMismatchToBadRequest() throws Exception {
        mockMvc.perform(get("/test/int-param").param("value", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is(ErrorCode.BAD_REQUEST.getCode())))
                .andExpect(jsonPath("$.traceId", is("trace-test-001")));
    }

    /**
     * 验证无法解析的 JSON 会转为 bad request。
     *
     * @throws Exception 测试异常
     */
    @Test
    void shouldConvertUnreadableJsonToBadRequest() throws Exception {
        mockMvc.perform(post("/test/json")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.code", is(ErrorCode.BAD_REQUEST.getCode())))
                .andExpect(jsonPath("$.traceId", is("trace-test-001")));
    }

    /**
     * 验证未处理异常日志中包含 traceId。
     *
     * @param output 捕获输出
     * @throws Exception 测试异常
     */
    @Test
    void shouldLogUnhandledExceptionWithTraceId(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/test/system").header(TraceContextFilter.TRACE_ID_HEADER, "trace-from-header"))
                .andExpect(status().isInternalServerError());

        org.assertj.core.api.Assertions.assertThat(output.getOut())
                .contains("traceId=trace-from-header")
                .contains("未处理异常");
    }

    /**
     * 固定 TraceId 的测试配置。
     *
     * @author cyc
     */
    @TestConfiguration
    static class TestTraceConfiguration {

        /**
         * 提供固定 TraceId 生成器。
         *
         * @return TraceId 生成器
         */
        @Bean
        @Primary
        TraceIdGenerator fixedTraceIdGenerator() {
            return () -> "trace-test-001";
        }
    }
}
