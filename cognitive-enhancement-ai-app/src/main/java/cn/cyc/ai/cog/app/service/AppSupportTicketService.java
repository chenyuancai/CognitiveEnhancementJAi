package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.app.dto.AppSupportTicketCreateRequest;
import cn.cyc.ai.cog.app.dto.AppSupportTicketVO;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.app.support.AppOpsLabelSupport;
import cn.cyc.ai.cog.platform.operations.domain.SupportTicket;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketSaveRequest;
import cn.cyc.ai.cog.platform.operations.service.SupportTicketService;
import org.springframework.stereotype.Service;

/**
 * C端支持Ticket服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppSupportTicketService {

    /** 支持Ticket服务。 */
    private final SupportTicketService supportTicketService;

    /**
     * 创建C端支持Ticket服务。
     *
     * @param supportTicketService 支持Ticket服务
     */
    public AppSupportTicketService(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<AppSupportTicketVO> page(SupportTicketPageQuery query) {
        Long userId = UserContext.currentUserId();
        query.setSubmitterUserId(userId);
        return supportTicketService.page(query).map(this::toVo);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public AppSupportTicketVO detail(Long id) {
        SupportTicket ticket = supportTicketService.detail(id);
        assertOwner(ticket);
        return toVo(ticket);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public AppSupportTicketVO create(AppSupportTicketCreateRequest request) {
        SupportTicketSaveRequest save = new SupportTicketSaveRequest();
        save.setTitle(request.getTitle());
        save.setBody(request.getBody());
        save.setCategory(request.getCategory() == null ? "GENERAL" : request.getCategory());
        save.setPriority(request.getPriority() == null ? "NORMAL" : request.getPriority());
        save.setSubmitterUserId(UserContext.currentUserId());
        return toVo(supportTicketService.create(save));
    }

    /**
     * 执行assertOwner。
     *
     * @param ticket ticket
     */
    private void assertOwner(SupportTicket ticket) {
        Long userId = UserContext.currentUserId();
        if (ticket.submitterUserId() == null || !ticket.submitterUserId().equals(userId)) {
            throw Errors.of(PlatformErrorCode.SUPPORT_TICKET_FORBIDDEN);
        }
    }

    /**
     * 转换为Vo。
     *
     * @param ticket ticket
     * @return 转换结果
     */
    private AppSupportTicketVO toVo(SupportTicket ticket) {
        AppSupportTicketVO vo = new AppSupportTicketVO();
        vo.setId(ticket.id());
        vo.setTicketNo(ticket.ticketNo());
        vo.setTitle(ticket.title());
        vo.setBody(ticket.body());
        vo.setCategory(ticket.category());
        vo.setStatus(ticket.status());
        vo.setStatusLabel(AppOpsLabelSupport.ticketStatusLabel(ticket.status()));
        vo.setPriority(ticket.priority());
        vo.setResolvedAt(ticket.resolvedAt());
        return vo;
    }
}
