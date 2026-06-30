# Java SDK 快速接入

`cognitive-enhancement-ai-sdk` 是平台外部接入便利层，第一版聚焦同步执行能力，避免业务方手写底层 HTTP 调用。

## Maven 依赖

```xml
<dependency>
    <groupId>cn.cyc.ai</groupId>
    <artifactId>cognitive-enhancement-ai-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 创建客户端

```java
CogSdkClient client = CogSdkClient.create(CogSdkClientConfig.builder()
        .baseUrl("http://localhost:8080")
        .bearerToken("your-jwt-token")
        .tenantCode("tenant-a")
        .traceId("trace-from-caller")
        .build());
```

说明：

- `baseUrl` 必填，不需要包含 `/api`。
- `bearerToken` 可选，设置后 SDK 自动发送 `Authorization: Bearer <token>`。
- `tenantCode` 可选，设置后 SDK 自动发送 `X-Tenant-Code`。
- `traceId` 可选，设置后 SDK 自动发送 `X-Trace-Id`。

## 执行能力

```java
CapabilityExecutionResult result = client.executeCapability(
        CapabilityExecutionRequest.builder()
                .capabilityCode("capability.qa.answer")
                .input("question", "什么是 Cognitive Enhancement AI?")
                .parameter("temperature", 0.3)
                .parameter("topP", 0.5)
                .build()
);

String answer = (String) result.output().get("answer");
```

## 错误处理

后端返回非 2xx 或统一响应 `success=false` 时，SDK 抛出 `CogSdkException`。

```java
try {
    client.executeCapability(request);
} catch (CogSdkException ex) {
    int httpStatus = ex.httpStatus();
    String code = ex.code();
    String traceId = ex.traceId();
}
```

## 当前边界

- 已支持：同步能力执行、租户透传、Trace 透传、JWT 透传、统一错误映射。
- 暂不支持：SSE、异步任务、自动重试、批量调用；这些能力留给后续流式/异步接口任务。
