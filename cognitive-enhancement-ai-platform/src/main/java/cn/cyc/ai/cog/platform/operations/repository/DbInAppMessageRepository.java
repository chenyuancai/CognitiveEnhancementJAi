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

/**
 * DbInC端消息仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbInAppMessageRepository implements InAppMessageRepository {

    /** inC端消息Mapper。 */
    private final InAppMessageMapper inAppMessageMapper;

    /**
     * 创建DbInC端消息仓储。
     *
     * @param inAppMessageMapper inC端消息Mapper
     */
    public DbInAppMessageRepository(InAppMessageMapper inAppMessageMapper) {
        this.inAppMessageMapper = inAppMessageMapper;
    }

    /**
     * 执行save。
     *
     * @param tenantId 租户 ID
     * @param userId 用户 ID
     * @param templateCode template编码
     * @param title 标题
     * @param content 内容
     * @return 执行结果
     */
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

    /**
     * 查询人用户列表。
     *
     * @param tenantId 租户 ID
     * @param userId 用户 ID
     * @param read read
     * @return 结果列表
     */
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

    /**
     * 执行markRead。
     *
     * @param tenantId 租户 ID
     * @param userId 用户 ID
     * @param messageId 消息 ID
     * @return 执行结果
     */
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

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
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
