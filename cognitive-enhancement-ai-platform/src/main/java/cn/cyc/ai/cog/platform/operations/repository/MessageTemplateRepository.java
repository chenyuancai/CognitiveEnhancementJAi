package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.MessageTemplate;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplatePageQuery;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplateSaveRequest;

/**
 * 消息Template仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface MessageTemplateRepository {

    PageResult<MessageTemplate> page(MessageTemplatePageQuery query);

    MessageTemplate findById(Long id);

    MessageTemplate findByCode(String templateCode);

    MessageTemplate create(MessageTemplateSaveRequest request);

    MessageTemplate update(Long id, MessageTemplateSaveRequest request);

    void delete(Long id);
}
