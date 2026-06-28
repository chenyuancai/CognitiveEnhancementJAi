package cn.cyc.ai.cog.sdk;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 平台 Java SDK 客户端测试。
 *
 * @author cyc
 */
class CogSdkClientTest {

    private final AtomicReference<RecordedRequest> recordedRequest = new AtomicReference<>();

    @Test
    void shouldExecuteCapabilityAndPropagateHeaders() {
        CogSdkClient client = CogSdkClient.create(CogSdkClientConfig.builder()
                        .baseUrl("http://127.0.0.1:8080")
                        .bearerToken("token-sdk")
                        .tenantCode("tenant-sdk")
                        .traceId("trace-sdk-001")
                        .build(),
                request -> {
                    recordedRequest.set(record(request));
                    return new SdkHttpResponse(200, """
                    {
                      "success": true,
                      "code": "00000",
                      "message": "success",
                      "traceId": "trace-sdk-001",
                      "timestamp": 1710000000000,
                      "data": {
                        "traceId": "trace-sdk-001",
                        "result": {
                          "status": "TOOL_INVOKED",
                          "message": "ok",
                          "output": {
                            "answer": "sdk ok"
                          }
                        }
                      }
                    }
                    """);
                },
                new com.fasterxml.jackson.databind.ObjectMapper());

        CapabilityExecutionResult result = client.executeCapability(CapabilityExecutionRequest.builder()
                .capabilityCode("capability.qa.answer")
                .input(Map.of("question", "hello"))
                .parameter("temperature", 0.3)
                .build());

        assertEquals("trace-sdk-001", result.traceId());
        assertEquals("TOOL_INVOKED", result.status());
        assertEquals("sdk ok", result.output().get("answer"));
        RecordedRequest request = recordedRequest.get();
        assertEquals("POST", request.method());
        assertEquals("Bearer token-sdk", request.header("Authorization"));
        assertEquals("tenant-sdk", request.header("X-Tenant-Code"));
        assertEquals("trace-sdk-001", request.header("X-Trace-Id"));
        assertTrue(request.body().contains("\"capabilityCode\":\"capability.qa.answer\""));
        assertTrue(request.body().contains("\"temperature\":0.3"));
    }

    @Test
    void shouldThrowSdkExceptionWhenApiReturnsFailure() {
        CogSdkClient client = CogSdkClient.create(CogSdkClientConfig.builder()
                        .baseUrl("http://127.0.0.1:8080")
                        .build(),
                request -> new SdkHttpResponse(409, """
                {
                  "success": false,
                  "code": "A0409",
                  "message": "能力未启用",
                  "traceId": "trace-sdk-error",
                  "timestamp": 1710000000000
                }
                """),
                new com.fasterxml.jackson.databind.ObjectMapper());

        CogSdkException exception = assertThrows(CogSdkException.class, () -> client.executeCapability(
                CapabilityExecutionRequest.builder()
                        .capabilityCode("capability.disabled")
                        .input(Map.of())
                        .build()
        ));

        assertEquals(409, exception.httpStatus());
        assertEquals("A0409", exception.code());
        assertEquals("trace-sdk-error", exception.traceId());
        assertTrue(exception.getMessage().contains("能力未启用"));
    }

    private RecordedRequest record(SdkHttpRequest request) throws IOException {
        return new RecordedRequest(
                request.method(),
                request.headers().get("Authorization"),
                request.headers().get("X-Tenant-Code"),
                request.headers().get("X-Trace-Id"),
                request.body()
        );
    }

    private record RecordedRequest(String method,
                                   String authorization,
                                   String tenantCode,
                                   String traceId,
                                   String body) {

        private String header(String name) {
            return switch (name) {
                case "Authorization" -> authorization;
                case "X-Tenant-Code" -> tenantCode;
                case "X-Trace-Id" -> traceId;
                default -> null;
            };
        }
    }
}
