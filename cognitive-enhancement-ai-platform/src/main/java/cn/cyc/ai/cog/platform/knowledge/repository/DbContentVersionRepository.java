package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentVersion;
import cn.cyc.ai.cog.platform.knowledge.entity.ContentVersionEntity;
import cn.cyc.ai.cog.platform.knowledge.mapper.ContentVersionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 内容版本仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbContentVersionRepository implements ContentVersionRepository {

    /** 内容版本号Mapper。 */
    private final ContentVersionMapper contentVersionMapper;

    /**
     * 创建Db内容版本号仓储。
     *
     * @param contentVersionMapper 内容版本号Mapper
     */
    public DbContentVersionRepository(ContentVersionMapper contentVersionMapper) {
        this.contentVersionMapper = contentVersionMapper;
    }

    /**
     * 执行append。
     *
     * @param version 版本号
     */
    @Override
    public void append(ContentVersion version) {
        ContentVersionEntity entity = new ContentVersionEntity();
        entity.setTenantId(TenantContext.currentTenantId());
        entity.setContentId(version.contentId());
        entity.setVersionNo(version.versionNo());
        entity.setTitle(version.title());
        entity.setSummary(version.summary());
        entity.setBody(version.body());
        entity.setMinLevelCode(version.minLevelCode());
        entity.setOperatorId(version.operatorId());
        entity.setCreateTime(version.createTime() == null ? LocalDateTime.now() : version.createTime());
        contentVersionMapper.insert(entity);
    }

    /**
     * 查询人内容ID列表。
     *
     * @param contentId 内容ID
     * @return 结果列表
     */
    @Override
    public List<ContentVersion> listByContentId(Long contentId) {
        return contentVersionMapper.selectList(new LambdaQueryWrapper<ContentVersionEntity>()
                        .eq(ContentVersionEntity::getContentId, contentId)
                        .orderByDesc(ContentVersionEntity::getVersionNo))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 查找Item。
     *
     * @param contentId 内容ID
     * @param versionNo 版本号，每次更新递增
     * @return 查找结果
     */
    @Override
    public Optional<ContentVersion> find(Long contentId, int versionNo) {
        ContentVersionEntity entity = contentVersionMapper.selectOne(new LambdaQueryWrapper<ContentVersionEntity>()
                .eq(ContentVersionEntity::getContentId, contentId)
                .eq(ContentVersionEntity::getVersionNo, versionNo));
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private ContentVersion toDomain(ContentVersionEntity entity) {
        return new ContentVersion(
                entity.getId(),
                entity.getContentId(),
                entity.getVersionNo() == null ? 0 : entity.getVersionNo(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getBody(),
                entity.getMinLevelCode(),
                entity.getOperatorId(),
                entity.getCreateTime()
        );
    }
}
