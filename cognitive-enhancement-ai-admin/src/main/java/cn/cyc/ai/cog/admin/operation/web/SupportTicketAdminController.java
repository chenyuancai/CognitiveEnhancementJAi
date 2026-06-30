package cn.cyc.ai.cog.admin.operation.web;

import cn.cyc.ai.cog.admin.operation.dto.SupportTicketVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.SupportTicket;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketSaveRequest;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketStatusUpdateRequest;
import cn.cyc.ai.cog.platform.operations.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支持Ticket管理后台接口
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "运营-客服工单", description = "客服工单管理")
@RestController
@RequestMapping("/api/admin/operations/support-tickets")
public class SupportTicketAdminController {

    /** 支持Ticket服务。 */
    private final SupportTicketService supportTicketService;

    /**
     * 创建支持Ticket管理后台接口。
     *
     * @param supportTicketService 支持Ticket服务
     */
    public SupportTicketAdminController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "工单分页")
    @RequirePermission("admin:ticket:read")
    @PostMapping("/page")
    public ApiResponse<PageResult<SupportTicketVO>> page(@RequestBody SupportTicketPageQuery query) {
        return ApiResponse.success(supportTicketService.page(query).map(this::toVo));
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Operation(summary = "工单详情")
    @RequirePermission("admin:ticket:read")
    @GetMapping("/{id}")
    public ApiResponse<SupportTicketVO> detail(@PathVariable Long id) {
        return ApiResponse.success(toVo(supportTicketService.detail(id)));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "新建工单")
    @RequirePermission("admin:ticket:update")
    @PostMapping
    public ApiResponse<SupportTicketVO> create(@Valid @RequestBody SupportTicketSaveRequest request) {
        return ApiResponse.success(toVo(supportTicketService.create(request)));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "编辑工单")
    @RequirePermission("admin:ticket:update")
    @PostMapping("/update")
    public ApiResponse<SupportTicketVO> update(@Valid @RequestBody SupportTicketSaveRequest request) {
        return ApiResponse.success(toVo(supportTicketService.update(request.getId(), request)));
    }

    /**
     * 更新状态。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "更新工单状态")
    @RequirePermission("admin:ticket:update")
    @PostMapping("/status")
    public ApiResponse<SupportTicketVO> updateStatus(@Valid @RequestBody SupportTicketStatusUpdateRequest request) {
        return ApiResponse.success(toVo(supportTicketService.updateStatus(request.getId(), request)));
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    @Operation(summary = "删除工单")
    @RequirePermission("admin:ticket:update")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        supportTicketService.delete(id);
        return ApiResponse.success(null);
    }

    /**
     * 转换为Vo。
     *
     * @param ticket ticket
     * @return 转换结果
     */
    private SupportTicketVO toVo(SupportTicket ticket) {
        SupportTicketVO vo = new SupportTicketVO();
        vo.setId(ticket.id());
        vo.setTicketNo(ticket.ticketNo());
        vo.setTitle(ticket.title());
        vo.setBody(ticket.body());
        vo.setCategory(ticket.category());
        vo.setStatus(ticket.status());
        vo.setPriority(ticket.priority());
        vo.setSubmitterUserId(ticket.submitterUserId());
        vo.setAssigneeUserId(ticket.assigneeUserId());
        vo.setResolvedAt(ticket.resolvedAt());
        return vo;
    }
}
