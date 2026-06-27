package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.platform.operations.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;

/**
 * 客服工单只读统计服务。
 */
@Service
public class SupportTicketStatsService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketStatsService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    /**
     * 统计待处理工单数（OPEN + IN_PROGRESS）。
     */
    public long countPending(Long tenantId) {
        return supportTicketRepository.countPending(tenantId);
    }
}
