package cn.cyc.ai.cog.admin.content.web;

import cn.cyc.ai.cog.admin.content.dto.AdminImportWorkflowDebugRequest;
import cn.cyc.ai.cog.admin.content.dto.AdminImportWorkflowDebugVO;
import cn.cyc.ai.cog.admin.content.service.AdminImportWorkflowService;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 导入工作流调试接口。
 */
@Tag(name = "内容-导入工作流", description = "知识文件导入流水线同步调试")
@RestController
@RequestMapping("/api/admin/import-workflow")
public class AdminImportWorkflowController {

    private final AdminImportWorkflowService adminImportWorkflowService;

    public AdminImportWorkflowController(AdminImportWorkflowService adminImportWorkflowService) {
        this.adminImportWorkflowService = adminImportWorkflowService;
    }

    @Operation(summary = "同步调试导入流水线", description = "不落导入任务表，直接执行解析并返回 contentId 与阶段日志。")
    @RequirePermission("admin:content:update")
    @PostMapping("/debug/sync")
    public ApiResponse<AdminImportWorkflowDebugVO> debugSync(@Valid @RequestBody AdminImportWorkflowDebugRequest request) {
        return ApiResponse.success(adminImportWorkflowService.debugSync(request));
    }
}
