package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.dto.AppSupportTicketCreateRequest;
import cn.cyc.ai.cog.app.dto.AppSupportTicketVO;
import cn.cyc.ai.cog.app.service.AppSupportTicketService;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "App-客服工单", description = "C 端提单与查单")
@RestController
@RequestMapping("/api/app/ops/support-tickets")
public class AppSupportTicketController {

    private final AppSupportTicketService appSupportTicketService;

    public AppSupportTicketController(AppSupportTicketService appSupportTicketService) {
        this.appSupportTicketService = appSupportTicketService;
    }

    @Operation(summary = "我的工单分页")
    @PostMapping("/page")
    public ApiResponse<PageResult<AppSupportTicketVO>> page(@RequestBody(required = false) SupportTicketPageQuery query) {
        return ApiResponse.success(appSupportTicketService.page(query == null ? new SupportTicketPageQuery() : query));
    }

    @Operation(summary = "工单详情")
    @GetMapping("/{id}")
    public ApiResponse<AppSupportTicketVO> detail(@PathVariable Long id) {
        return ApiResponse.success(appSupportTicketService.detail(id));
    }

    @Operation(summary = "提交工单")
    @PostMapping
    public ApiResponse<AppSupportTicketVO> create(@Valid @RequestBody AppSupportTicketCreateRequest request) {
        return ApiResponse.success(appSupportTicketService.create(request));
    }
}
