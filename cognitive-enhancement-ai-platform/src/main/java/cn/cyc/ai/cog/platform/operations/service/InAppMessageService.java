package cn.cyc.ai.cog.platform.operations.service;

import cn.cyc.ai.cog.platform.operations.domain.InAppMessage;
import cn.cyc.ai.cog.platform.operations.repository.InAppMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InAppMessageService {

    private final InAppMessageRepository inAppMessageRepository;

    public InAppMessageService(InAppMessageRepository inAppMessageRepository) {
        this.inAppMessageRepository = inAppMessageRepository;
    }

    public List<InAppMessage> listForUser(Long tenantId, Long userId, Boolean read) {
        return inAppMessageRepository.listByUser(tenantId, userId, read);
    }

    public InAppMessage markRead(Long tenantId, Long userId, Long messageId) {
        return inAppMessageRepository.markRead(tenantId, userId, messageId);
    }
}
