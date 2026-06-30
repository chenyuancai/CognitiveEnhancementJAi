package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.SupportTicket;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketSaveRequest;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketStatusUpdateRequest;

/**
 * 支持Ticket仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface SupportTicketRepository {

    PageResult<SupportTicket> page(SupportTicketPageQuery query);

    SupportTicket findById(Long id);

    long countPending(Long tenantId);

    SupportTicket create(SupportTicketSaveRequest request);

    SupportTicket update(Long id, SupportTicketSaveRequest request);

    SupportTicket updateStatus(Long id, SupportTicketStatusUpdateRequest request);

    void delete(Long id);
}
