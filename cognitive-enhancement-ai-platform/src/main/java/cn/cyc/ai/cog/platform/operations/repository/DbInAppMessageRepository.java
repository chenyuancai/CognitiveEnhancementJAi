package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.platform.operations.domain.InAppMessage;
import cn.cyc.ai.cog.platform.operations.entity.InAppMessageEntity;
import cn.cyc.ai.cog.platform.operations.mapper.InAppMessageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DbInAppMessageRepository implements InAppMessageRepository {

    private final InAppMessageMapper inAppMessageMapper;

    public DbInAppMessageRepository(InAppMessageMapper inAppMessageMapper) {
        this.inAppMessageMapper = inAppMessageMapper;
    }

    @Override
    public InAppMessage save(Long tenantId, Long userId, String templateCode, String title, String content) {
        InAppMessageEntity entity = new InAppMessageEntity();
        entity.setTenantId(tenantId == null ? 1L : tenantId);
        entity.setUserId(userId);
        entity.setTemplateCode(templateCode);
        entity.setTitle(title);
        entity.setContent(content);
        entity.setReadFlag(0);
        entity.setDeleted(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        inAppMessageMapper.insert(entity);
        return toDomain(entity);
    }

    @Override
    public List<InAppMessage> listByUser(Long tenantId, Long userId, Boolean read) {
        LambdaQueryWrapper<InAppMessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InAppMessageEntity::getTenantId, tenantId == null ? 1L : tenantId);
        wrapper.eq(InAppMessageEntity::getUserId, userId);
        if (read != null) {
            wrapper.eq(InAppMessageEntity::getReadFlag, Boolean.TRUE.equals(read) ? 1 : 0);
        }
        wrapper.orderByDesc(InAppMessageEntity::getId);
        return inAppMessageMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public InAppMessage markRead(Long tenantId, Long userId, Long messageId) {
        InAppMessageEntity entity = inAppMessageMapper.selectById(messageId);
        if (entity == null || !userId.equals(entity.getUserId())) {
            throw Errors.of(PlatformErrorCode.IN_APP_MESSAGE_NOT_FOUND);
        }
        if (tenantId != null && !tenantId.equals(entity.getTenantId())) {
            throw Errors.of(PlatformErrorCode.IN_APP_MESSAGE_NOT_FOUND);
        }
        inAppMessageMapper.update(null, new LambdaUpdateWrapper<InAppMessageEntity>()
                .eq(InAppMessageEntity::getId, messageId)
                .set(InAppMessageEntity::getReadFlag, 1)
                .set(InAppMessageEntity::getUpdateTime, LocalDateTime.now()));
        entity.setReadFlag(1);
        return toDomain(entity);
    }

    private InAppMessage toDomain(InAppMessageEntity entity) {
        return new InAppMessage(
                entity.getId(),
                entity.getTenantId(),
                entity.getUserId(),
                entity.getTemplateCode(),
                entity.getTitle(),
                entity.getContent(),
                entity.getReadFlag() != null && entity.getReadFlag() == 1,
                entity.getCreateTime()
        );
    }
}
