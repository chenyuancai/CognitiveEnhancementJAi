package cn.cyc.ai.cog.admin.operation.web;

import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardQuery;
import cn.cyc.ai.cog.admin.operation.dto.OperationDashboardResult;
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
 * 运营看板（PRD 6.1）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "运营-看板", description = "核心指标聚合、用户增长与会员分布")
@RestController
@RequestMapping("/api/admin/operations/dashboard")
public class OperationDashboardAdminController {

    /** operationDashboard服务。 */
    private final OperationDashboardService operationDashboardService;

    /**
     * 创建OperationDashboard管理后台接口。
     *
     * @param operationDashboardService operationDashboard服务
     */
    public OperationDashboardAdminController(OperationDashboardService operationDashboardService) {
        this.operationDashboardService = operationDashboardService;
    }

    /**
     * 执行overview。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "运营看板概览", description = "支持 preset=TODAY|LAST_7_DAYS|LAST_30_DAYS")
    @RequirePermission("admin:user:view")
    @PostMapping("/query")
    public ApiResponse<OperationDashboardResult> overview(@RequestBody OperationDashboardQuery query) {
        return ApiResponse.success(operationDashboardService.build(query));
    }
}
