package cn.cyc.ai.cog.admin.system.web;

import cn.cyc.ai.cog.admin.system.service.SystemHealthService;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * SystemHealth管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "系统-健康检查", description = "管理后台健康探针")
@RestController
@RequestMapping("/api/admin/system")
public class SystemHealthAdminController {

    /** systemHealth服务。 */
    private final SystemHealthService systemHealthService;

    /**
     * 创建SystemHealth管理后台接口。
     *
     * @param systemHealthService systemHealth服务
     */
    public SystemHealthAdminController(SystemHealthService systemHealthService) {
        this.systemHealthService = systemHealthService;
    }

    @Operation(summary = "健康检查", description = "返回 services[] 与 checkedAt，供 CMS 系统设置页使用。")
    @SecurityRequirements
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(systemHealthService.buildReport());
    }

    @Operation(summary = "刷新健康检查", description = "触发全量探针并返回最新报告。")
    @SecurityRequirements
    @PostMapping("/health/refresh")
    public ApiResponse<Map<String, Object>> refreshHealth() {
        return ApiResponse.success(systemHealthService.buildReport());
    }
}
