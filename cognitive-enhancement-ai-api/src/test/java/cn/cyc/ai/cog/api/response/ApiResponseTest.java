package cn.cyc.ai.cog.api.response;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiResponseTest {

    @Test
    void shouldCreateSuccessResponseWithDataAndTraceId() {
        ApiResponse<String> response = ApiResponse.success("payload", "trace-001");

        assertTrue(response.success());
        assertEquals(ErrorCode.SUCCESS.getCode(), response.code());
        assertEquals("成功", response.message());
        assertEquals("payload", response.data());
        assertEquals("trace-001", response.traceId());
        assertNotNull(response.timestamp());
    }

    @Test
    void shouldCreateFailureResponseFromErrorCode() {
        ApiResponse<Void> response = ApiResponse.failure(ErrorCode.BAD_REQUEST, null);

        assertFalse(response.success());
        assertEquals(ErrorCode.BAD_REQUEST.getCode(), response.code());
        assertEquals(ErrorCode.BAD_REQUEST.getMessage(), response.message());
        assertNull(response.data());
        assertNull(response.traceId());
        assertNotNull(response.timestamp());
    }

    @Test
    void shouldAllowOverrideFailureMessage() {
        ApiResponse<Void> response = ApiResponse.failure(ErrorCode.BUSINESS_ERROR, "自定义失败原因", "trace-002");

        assertFalse(response.success());
        assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), response.code());
        assertEquals("自定义失败原因", response.message());
        assertEquals("trace-002", response.traceId());
    }
}
