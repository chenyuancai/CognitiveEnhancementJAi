package cn.cyc.ai.cog.admin.ai.web;

import cn.cyc.ai.cog.admin.ai.dto.AiRoutingOverviewResult;
import cn.cyc.ai.cog.admin.ai.service.AiRoutingOverviewService;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 控制台 - 路由与治理总览（PRD 5.x）。
 */
@Tag(name = "AI-路由治理", description = "模型状态、熔断治理与 Capability 灰度路由摘要")
@RestController
@RequestMapping("/api/admin/ai/routing-overview")
public class AiRoutingAdminController {

    private final AiRoutingOverviewService aiRoutingOverviewService;

    public AiRoutingAdminController(AiRoutingOverviewService aiRoutingOverviewService) {
        this.aiRoutingOverviewService = aiRoutingOverviewService;
    }

    @Operation(summary = "AI 路由治理总览")
    @RequirePermission("admin:order:update")
    @GetMapping
    public ApiResponse<AiRoutingOverviewResult> overview() {
        return ApiResponse.success(aiRoutingOverviewService.build());
    }
}
