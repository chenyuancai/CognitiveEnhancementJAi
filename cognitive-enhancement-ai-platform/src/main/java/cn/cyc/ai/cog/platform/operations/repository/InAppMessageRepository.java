package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.platform.operations.domain.InAppMessage;

import java.util.List;

public interface InAppMessageRepository {

    InAppMessage save(Long tenantId, Long userId, String templateCode, String title, String content);

    List<InAppMessage> listByUser(Long tenantId, Long userId, Boolean read);

    InAppMessage markRead(Long tenantId, Long userId, Long messageId);
}
