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

/**
 * C端支持Ticket接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-客服工单", description = "C 端提单与查单")
@RestController
@RequestMapping("/api/app/ops/support-tickets")
public class AppSupportTicketController {

    /** C端支持Ticket服务。 */
    private final AppSupportTicketService appSupportTicketService;

    /**
     * 创建C端支持Ticket接口。
     *
     * @param appSupportTicketService C端支持Ticket服务
     */
    public AppSupportTicketController(AppSupportTicketService appSupportTicketService) {
        this.appSupportTicketService = appSupportTicketService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "我的工单分页")
    @PostMapping("/page")
    public ApiResponse<PageResult<AppSupportTicketVO>> page(@RequestBody(required = false) SupportTicketPageQuery query) {
        return ApiResponse.success(appSupportTicketService.page(query == null ? new SupportTicketPageQuery() : query));
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Operation(summary = "工单详情")
    @GetMapping("/{id}")
    public ApiResponse<AppSupportTicketVO> detail(@PathVariable Long id) {
        return ApiResponse.success(appSupportTicketService.detail(id));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "提交工单")
    @PostMapping
    public ApiResponse<AppSupportTicketVO> create(@Valid @RequestBody AppSupportTicketCreateRequest request) {
        return ApiResponse.success(appSupportTicketService.create(request));
    }
}
