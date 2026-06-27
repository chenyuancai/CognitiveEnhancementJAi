package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.MessageTemplate;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplatePageQuery;
import cn.cyc.ai.cog.platform.operations.dto.MessageTemplateSaveRequest;
import cn.cyc.ai.cog.platform.operations.entity.MessageTemplateEntity;
import cn.cyc.ai.cog.platform.operations.mapper.MessageTemplateMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 消息模板仓储 MyBatis 实现。
 */
@Repository
public class DbMessageTemplateRepository implements MessageTemplateRepository {

    /** 消息模板 Mapper */
    private final MessageTemplateMapper messageTemplateMapper;

    /**
     * @param messageTemplateMapper 消息模板 Mapper
     */
    public DbMessageTemplateRepository(MessageTemplateMapper messageTemplateMapper) {
        this.messageTemplateMapper = messageTemplateMapper;
    }

    @Override
    public PageResult<MessageTemplate> page(MessageTemplatePageQuery query) {
        LambdaQueryWrapper<MessageTemplateEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(MessageTemplateEntity::getTemplateCode, query.getKeyword())
                    .or().like(MessageTemplateEntity::getTemplateName, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getChannel())) {
            wrapper.eq(MessageTemplateEntity::getChannel, query.getChannel());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(MessageTemplateEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(MessageTemplateEntity::getId);
        Page<MessageTemplateEntity> page = messageTemplateMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public MessageTemplate findById(Long id) {
        return toDomain(require(id));
    }

    @Override
    public MessageTemplate findByCode(String templateCode) {
        MessageTemplateEntity entity = messageTemplateMapper.selectOne(new LambdaQueryWrapper<MessageTemplateEntity>()
                .eq(MessageTemplateEntity::getTemplateCode, templateCode)
                .last("LIMIT 1"));
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.MESSAGE_TEMPLATE_NOT_FOUND);
        }
        return toDomain(entity);
    }

    @Override
    public MessageTemplate create(MessageTemplateSaveRequest request) {
        checkCodeUnique(request.getTemplateCode(), null);
        MessageTemplateEntity entity = map(request, new MessageTemplateEntity());
        messageTemplateMapper.insert(entity);
        return toDomain(entity);
    }

    @Override
    public MessageTemplate update(Long id, MessageTemplateSaveRequest request) {
        checkCodeUnique(request.getTemplateCode(), id);
        MessageTemplateEntity entity = map(request, require(id));
        messageTemplateMapper.updateById(entity);
        return toDomain(entity);
    }

    @Override
    public void delete(Long id) {
        require(id);
        messageTemplateMapper.deleteById(id);
    }

    private void checkCodeUnique(String code, Long excludeId) {
        MessageTemplateEntity existing = messageTemplateMapper.selectOne(new LambdaQueryWrapper<MessageTemplateEntity>()
                .eq(MessageTemplateEntity::getTemplateCode, code)
                .last("LIMIT 1"));
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw Errors.of(PlatformErrorCode.MESSAGE_TEMPLATE_CODE_EXISTS);
        }
    }

    private MessageTemplateEntity map(MessageTemplateSaveRequest request, MessageTemplateEntity entity) {
        entity.setTemplateCode(request.getTemplateCode().trim());
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setChannel(request.getChannel().trim());
        entity.setContent(request.getContent());
        entity.setVariableSchema(request.getVariableSchema());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : CommonConstants.STATUS_ENABLED);
        return entity;
    }

    private MessageTemplateEntity require(Long id) {
        MessageTemplateEntity entity = messageTemplateMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.MESSAGE_TEMPLATE_NOT_FOUND);
        }
        return entity;
    }

    private MessageTemplate toDomain(MessageTemplateEntity entity) {
        return new MessageTemplate(
                entity.getId(),
                entity.getTemplateCode(),
                entity.getTemplateName(),
                entity.getChannel(),
                entity.getContent(),
                entity.getVariableSchema(),
                entity.getStatus()
        );
    }
}
