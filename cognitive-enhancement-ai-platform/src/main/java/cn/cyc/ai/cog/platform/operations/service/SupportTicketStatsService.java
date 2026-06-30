package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.platform.operations.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;

/**
 * 客服工单只读统计服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class SupportTicketStatsService {

    /** 支持Ticket仓储。 */
    private final SupportTicketRepository supportTicketRepository;

    /**
     * 创建支持TicketStats服务。
     *
     * @param supportTicketRepository 支持Ticket仓储
     */
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
