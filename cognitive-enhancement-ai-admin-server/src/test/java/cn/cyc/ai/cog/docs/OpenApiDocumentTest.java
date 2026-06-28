package cn.cyc.ai.cog.docs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OpenAPI 文档交付校验。
 *
 * @author cyc
 */
class OpenApiDocumentTest {

    @Test
    void shouldDocumentPublicApiEntryPoints() throws IOException {
        Path docsDir = Path.of("..", "docs");
        String openApi = Files.readString(docsDir.resolve("openapi.yaml"));
        String apiReference = Files.readString(docsDir.resolve("api-reference.md"));

        List<String> requiredFragments = List.of(
                "openapi: 3.0.3",
                "/api/auth/login:",
                "/api/runtime/capabilities/execute:",
                "/api/runtime/capabilities/execute/stream:",
                "/api/runtime/tools/debug-invoke:",
                "/api/runtime/observations/executions:",
                "/api/runtime/observations/stats:",
                "/api/runtime/observations/traces/{traceId}/spans:",
                "/api/runtime/observations/audit-logs:",
                "/api/runtime/sessions:",
                "/api/runtime/sessions/{sessionId}/messages:",
                "/api/runtime/feedback:",
                "/api/runtime/knowledge/fragments:",
                "/api/runtime/knowledge/bindings:",
                "/api/runtime/knowledge/retrieve:",
                "/api/runtime/files:",
                "/api/runtime/files/parse:",
                "/api/runtime/files/{fileId}/parse-result:",
                "/api/runtime/usage/account:",
                "/api/runtime/models/check:",
                "/api/runtime/models/governance:",
                "ModelGovernanceStateResult",
                "TraceSpanResult",
                "AuditLogRecordResult",
                "CreateSessionRequest",
                "SubmitFeedbackRequest",
                "CreateKnowledgeFragmentRequest",
                "RegisterFileUploadRequest",
                "RUNTIME_FAILURE",
                "/api/admin/harness/run:",
                "/api/admin/auth/me:",
                "/api/admin/operations/dashboard:",
                "/api/admin/ai/cost-dashboard:",
                "/api/admin/ai/routing-overview:",
                "UserPageQuery",
                "/api/admin/users/page:",
                "/api/admin/billing/orders:",
                "/api/admin/billing/subscription-packages:",
                "/api/admin/content/import-jobs:",
                "/api/admin/operations/banners:",
                "/api/admin/operations/announcements:",
                "/api/admin/operations/message-templates:",
                "/api/admin/membership/levels:",
                "/api/admin/orgs:",
                "/api/admin/iam/tenants:",
                "/api/admin/permissions/tree:",
                "/api/admin/system/audit-logs:",
                "/api/admin/system/health:",
                "CreateOrganizationRequest",
                "/api/admin/billing/orders/cancel:",
                "/api/admin/workbench:",
                "/api/admin/workbench/dashboard:",
                "/api/admin/workbench/overview:",
                "/api/admin/workbench/trends:",
                "/api/admin/workbench/todo:",
                "/api/app/auth/register:",
                "/api/app/knowledge/contents:",
                "/api/app/knowledge/contents/{id}:",
                "/api/app/knowledge/packages:",
                "/api/app/knowledge/packages/{id}/tree:",
                "/api/app/learning/modes:",
                "/api/app/learning/invoke:",
                "/api/app/ops/banners:",
                "/api/app/ops/announcements:",
                "/api/admin/content/contents/{id}/versions:",
                "/api/admin/content/contents/rollback:",
                "workbench:view",
                "AiRoutingOverviewResult",
                "CapabilityRoutingItem",
                "DashboardResult",
                "WorkbenchResult",
                "WorkbenchTodoItem",
                "DashboardOverview",
                "DashboardTodo",
                "TrendSeries",
                "AppLearningInvokeRequest",
                "AppContentSummaryVO",
                "/api/center/model-providers/page:",
                "/api/center/model-providers/all:",
                "ModelProviderUpsertRequest",
                "ModelProviderBindingRequest",
                "/api/center/models:",
                "/api/center/prompts/{code}/versions:",
                "/api/center/prompts/publish:",
                "/api/center/prompts/gray:",
                "/api/center/capabilities/{code}/versions:",
                "/api/center/capabilities/publish:",
                "/api/center/capabilities/gray:",
                "/api/center/capabilities/tenants/configure:",
                "/api/center/capabilities:",
                "CenterPageResult",
                "PromptLifecycleStatus",
                "PromptReleasePointer",
                "CapabilityLifecycleStatus",
                "CapabilityReleasePointer",
                "CAPABILITY_DISABLED",
                "ToolDebugInvokeRequest",
                "ToolDebugInvokeResponse",
                "X-Tenant-Code",
                "X-Trace-Id"
        );
        for (String fragment : requiredFragments) {
            assertTrue(openApi.contains(fragment), "OpenAPI 缺少片段: " + fragment);
        }
        assertTrue(apiReference.contains("统一响应结构"));
        assertTrue(apiReference.contains("鉴权与租户"));
        assertTrue(apiReference.contains("Runtime 能力执行"));
        assertTrue(apiReference.contains("Runtime 流式执行"));
        assertTrue(apiReference.contains("Runtime Tool 调试调用"));
        assertTrue(apiReference.contains("TOOL_DEBUG_INVOKE"));
        assertTrue(apiReference.contains("Runtime 用量额度"));
        assertTrue(apiReference.contains("Prompt 发布与灰度"));
        assertTrue(apiReference.contains("Capability 发布与版本控制"));
        assertTrue(apiReference.contains("模型治理"));
        assertTrue(apiReference.contains("Trace Span 与审计"));
        assertTrue(apiReference.contains("Runtime 会话与反馈"));
        assertTrue(apiReference.contains("Runtime 知识库与文件"));
        assertTrue(apiReference.contains("Tool Adapter 与 MCP 增强"));
        assertTrue(apiReference.contains("PolicyHarness 策略治理"));
        assertTrue(apiReference.contains("管理工作台看板"));
        assertTrue(apiReference.contains("C 端知识内容"));
        assertTrue(apiReference.contains("C 端学习链路"));
        assertTrue(apiReference.contains("C 端计费与支付"));
        assertTrue(apiReference.contains("AppPayOrderResultVO"));
        assertTrue(apiReference.contains("pay-callback"));
        assertTrue(apiReference.contains("站内信"));
        assertTrue(openApi.contains("/api/app/billing/orders/pay:"));
        assertTrue(openApi.contains("/api/app/billing/pay-callback:"));
        assertTrue(openApi.contains("/api/app/ops/in-app-messages:"));
        assertTrue(openApi.contains("/api/app/ops/support-tickets:"));
        assertTrue(openApi.contains("/api/admin/operations/message-templates/send:"));
        assertTrue(openApi.contains("/api/admin/quota/accounts/{accountId}/member-allocs:"));
        assertTrue(openApi.contains("AppPayOrderResultVO"));
        assertTrue(openApi.contains("PaymentCallbackRequest"));
        assertTrue(apiReference.contains("订单取消"));
        assertTrue(apiReference.contains("OpenTelemetry TraceSpan 导出"));
        assertTrue(apiReference.contains("SkillLoader / OutputGovernance 增强"));
        assertTrue(apiReference.contains("RUNTIME_FAILURE"));
        assertTrue(apiReference.contains("CAPABILITY_DISABLED"));
        assertTrue(apiReference.contains("错误码与排障指南"));
        assertTrue(apiReference.contains("Trace 排障流程"));
        assertTrue(apiReference.contains("A0429"));
        assertTrue(apiReference.contains("SSE `FAILED`"));
    }
}
