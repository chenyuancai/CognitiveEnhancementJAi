package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.SupportTicket;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketSaveRequest;
import cn.cyc.ai.cog.platform.operations.dto.SupportTicketStatusUpdateRequest;
import cn.cyc.ai.cog.platform.operations.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;

/**
 * 支持Ticket服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class SupportTicketService {

    /** 支持Ticket仓储。 */
    private final SupportTicketRepository supportTicketRepository;

    /**
     * 创建支持Ticket服务。
     *
     * @param supportTicketRepository 支持Ticket仓储
     */
    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    public PageResult<SupportTicket> page(SupportTicketPageQuery query) {
        return supportTicketRepository.page(query);
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    public SupportTicket detail(Long id) {
        return supportTicketRepository.findById(id);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public SupportTicket create(SupportTicketSaveRequest request) {
        return supportTicketRepository.create(request);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public SupportTicket update(Long id, SupportTicketSaveRequest request) {
        return supportTicketRepository.update(id, request);
    }

    /**
     * 更新状态。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    public SupportTicket updateStatus(Long id, SupportTicketStatusUpdateRequest request) {
        return supportTicketRepository.updateStatus(id, request);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    public void delete(Long id) {
        supportTicketRepository.delete(id);
    }
}
