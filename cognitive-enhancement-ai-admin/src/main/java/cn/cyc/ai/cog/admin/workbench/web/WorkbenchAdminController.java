package cn.cyc.ai.cog.admin.workbench.web;

import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardOverview;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardResult;
import cn.cyc.ai.cog.admin.workbench.dto.DashboardTodo;
import cn.cyc.ai.cog.admin.workbench.dto.TrendSeries;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchQuery;
import cn.cyc.ai.cog.admin.workbench.dto.WorkbenchResult;
import cn.cyc.ai.cog.admin.workbench.service.WorkbenchPersonalizationService;
import cn.cyc.ai.cog.admin.workbench.service.WorkbenchService;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工作台看板接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "工作台", description = "概览卡片、趋势曲线、待办告警与 AI 看板内联聚合")
@RestController
@RequestMapping("/api/admin/workbench")
public class WorkbenchAdminController {

    /** 工作台聚合服务 */
    private final WorkbenchService workbenchService;

    /** 角色化首页服务 */
    private final WorkbenchPersonalizationService workbenchPersonalizationService;

    /**
     * @param workbenchService                 工作台聚合服务
     * @param workbenchPersonalizationService  角色化首页服务
     */
    public WorkbenchAdminController(WorkbenchService workbenchService,
                                    WorkbenchPersonalizationService workbenchPersonalizationService) {
        this.workbenchService = workbenchService;
        this.workbenchPersonalizationService = workbenchPersonalizationService;
    }

    /**
     * 执行home。
     * @return 执行结果
     */
    @Operation(summary = "角色化工作台首页", description = "按当前用户角色返回待办、指标与快捷入口；需要 workbench:view。")
    @RequirePermission("workbench:view")
    @GetMapping
    public ApiResponse<WorkbenchResult> home() {
        return ApiResponse.success(workbenchPersonalizationService.personalized());
    }

    /**
     * 执行dashboard。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "工作台看板聚合", description = "默认近 7 日；refresh=true 绕过 60s 缓存。需要 workbench:view 权限点。")
    @RequirePermission("workbench:view")
    @PostMapping("/dashboard/query")
    public ApiResponse<DashboardResult> dashboard(@RequestBody WorkbenchQuery query) {
        return ApiResponse.success(workbenchService.dashboard(query));
    }

    /**
     * 执行overview。
     * @return 执行结果
     */
    @Operation(summary = "概览卡片", description = "需要 workbench:view 权限点。")
    @RequirePermission("workbench:view")
    @GetMapping("/overview")
    public ApiResponse<DashboardOverview> overview() {
        return ApiResponse.success(workbenchService.overview());
    }

    /**
     * 执行trends。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "趋势曲线", description = "默认近 7 日，支持 from/to 自定义。需要 workbench:view 权限点。")
    @RequirePermission("workbench:view")
    @PostMapping("/trends/query")
    public ApiResponse<List<TrendSeries>> trends(@RequestBody WorkbenchQuery query) {
        return ApiResponse.success(workbenchService.trends(query));
    }

    /**
     * 转换为do。
     * @return 转换结果
     */
    @Operation(summary = "待办/告警", description = "需要 workbench:view 权限点。")
    @RequirePermission("workbench:view")
    @GetMapping("/todo")
    public ApiResponse<DashboardTodo> todo() {
        return ApiResponse.success(workbenchService.todo());
    }
}
