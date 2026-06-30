package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.platform.operations.domain.InAppMessage;
import cn.cyc.ai.cog.platform.operations.repository.InAppMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * InC端消息服务
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class InAppMessageService {

    /** inC端消息仓储。 */
    private final InAppMessageRepository inAppMessageRepository;

    /**
     * 创建InC端消息服务。
     *
     * @param inAppMessageRepository inC端消息仓储
     */
    public InAppMessageService(InAppMessageRepository inAppMessageRepository) {
        this.inAppMessageRepository = inAppMessageRepository;
    }

    /**
     * 查询For用户列表。
     *
     * @param tenantId 租户 ID
     * @param userId 用户 ID
     * @param read read
     * @return 结果列表
     */
    public List<InAppMessage> listForUser(Long tenantId, Long userId, Boolean read) {
        return inAppMessageRepository.listByUser(tenantId, userId, read);
    }

    /**
     * 执行markRead。
     *
     * @param tenantId 租户 ID
     * @param userId 用户 ID
     * @param messageId 消息 ID
     * @return 执行结果
     */
    public InAppMessage markRead(Long tenantId, Long userId, Long messageId) {
        return inAppMessageRepository.markRead(tenantId, userId, messageId);
    }
}
