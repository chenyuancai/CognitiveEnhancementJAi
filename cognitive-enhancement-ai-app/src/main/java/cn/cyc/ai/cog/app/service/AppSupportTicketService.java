package cn.cyc.ai.cog.app.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.app.dto.AppSupportTicketCreateRequest;
import cn.cyc.ai.cog.app.dto.AppSupportTicketVO;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.SupportTicket;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketSaveRequest;
import cn.cyc.ai.cog.platform.operations.service.SupportTicketService;
import org.springframework.stereotype.Service;

@Service
public class AppSupportTicketService {

    private final SupportTicketService supportTicketService;

    public AppSupportTicketService(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    public PageResult<AppSupportTicketVO> page(SupportTicketPageQuery query) {
        Long userId = UserContext.currentUserId();
        query.setSubmitterUserId(userId);
        return supportTicketService.page(query).map(this::toVo);
    }

    public AppSupportTicketVO detail(Long id) {
        SupportTicket ticket = supportTicketService.detail(id);
        assertOwner(ticket);
        return toVo(ticket);
    }

    public AppSupportTicketVO create(AppSupportTicketCreateRequest request) {
        SupportTicketSaveRequest save = new SupportTicketSaveRequest();
        save.setTitle(request.getTitle());
        save.setBody(request.getBody());
        save.setCategory(request.getCategory() == null ? "GENERAL" : request.getCategory());
        save.setPriority(request.getPriority() == null ? "NORMAL" : request.getPriority());
        save.setSubmitterUserId(UserContext.currentUserId());
        return toVo(supportTicketService.create(save));
    }

    private void assertOwner(SupportTicket ticket) {
        Long userId = UserContext.currentUserId();
        if (ticket.submitterUserId() == null || !ticket.submitterUserId().equals(userId)) {
            throw Errors.of(PlatformErrorCode.SUPPORT_TICKET_FORBIDDEN);
        }
    }

    private AppSupportTicketVO toVo(SupportTicket ticket) {
        AppSupportTicketVO vo = new AppSupportTicketVO();
        vo.setId(ticket.id());
        vo.setTicketNo(ticket.ticketNo());
        vo.setTitle(ticket.title());
        vo.setBody(ticket.body());
        vo.setCategory(ticket.category());
        vo.setStatus(ticket.status());
        vo.setPriority(ticket.priority());
        vo.setResolvedAt(ticket.resolvedAt());
        return vo;
    }
}
