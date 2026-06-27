package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.SupportTicket;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketSaveRequest;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketStatusUpdateRequest;
import cn.cyc.ai.cog.platform.operations.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    public PageResult<SupportTicket> page(SupportTicketPageQuery query) {
        return supportTicketRepository.page(query);
    }

    public SupportTicket detail(Long id) {
        return supportTicketRepository.findById(id);
    }

    public SupportTicket create(SupportTicketSaveRequest request) {
        return supportTicketRepository.create(request);
    }

    public SupportTicket update(Long id, SupportTicketSaveRequest request) {
        return supportTicketRepository.update(id, request);
    }

    public SupportTicket updateStatus(Long id, SupportTicketStatusUpdateRequest request) {
        return supportTicketRepository.updateStatus(id, request);
    }

    public void delete(Long id) {
        supportTicketRepository.delete(id);
    }
}
