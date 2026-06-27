package cn.cyc.ai.cog.admin.ai.web;

import cn.cyc.ai.cog.admin.ai.dto.AiCostDashboardQuery;
import cn.cyc.ai.cog.admin.ai.dto.AiCostDashboardResult;
import cn.cyc.ai.cog.admin.operation.service.OperationDashboardService;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 控制台 - 成本看板（PRD 5.x）。
 */
@Tag(name = "AI-成本看板", description = "Token 消耗趋势与能力调用分布")
@RestController
@RequestMapping("/api/admin/ai/cost-dashboard")
public class AiCostDashboardAdminController {

    private final OperationDashboardService operationDashboardService;

    public AiCostDashboardAdminController(OperationDashboardService operationDashboardService) {
        this.operationDashboardService = operationDashboardService;
    }

    @Operation(summary = "AI 成本看板", description = "支持 preset=TODAY|LAST_7_DAYS|LAST_30_DAYS")
    @RequirePermission("admin:order:update")
    @PostMapping("/query")
    public ApiResponse<AiCostDashboardResult> overview(@RequestBody AiCostDashboardQuery query) {
        return ApiResponse.success(AiCostDashboardResult.from(
                operationDashboardService.build(AiCostDashboardResult.toQuery(query))));
    }
}
