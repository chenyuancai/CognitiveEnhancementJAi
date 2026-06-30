package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.api.enums.AnnouncementStatus;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Announcement;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.AnnouncementSaveRequest;
import cn.cyc.ai.cog.platform.operations.entity.AnnouncementEntity;
import cn.cyc.ai.cog.platform.operations.mapper.AnnouncementMapper;
import cn.cyc.ai.cog.platform.operations.support.AnnouncementAudienceSupport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbAnnouncementRepository implements AnnouncementRepository {

    /** 公告 Mapper */
    private final AnnouncementMapper announcementMapper;

    /** 公告受众辅助 */
    private final AnnouncementAudienceSupport announcementAudienceSupport;

    /**
     * @param announcementMapper           公告 Mapper
     * @param announcementAudienceSupport  公告受众辅助
     */
    public DbAnnouncementRepository(AnnouncementMapper announcementMapper,
                                    AnnouncementAudienceSupport announcementAudienceSupport) {
        this.announcementMapper = announcementMapper;
        this.announcementAudienceSupport = announcementAudienceSupport;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<Announcement> page(AnnouncementPageQuery query) {
        LambdaQueryWrapper<AnnouncementEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(AnnouncementEntity::getTitle, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(AnnouncementEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(AnnouncementEntity::getId);
        Page<AnnouncementEntity> page = announcementMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 查找人ID。
     *
     * @param id 主键 ID
     * @return 查找结果
     */
    @Override
    public Announcement findById(Long id) {
        return toDomain(require(id));
    }

    /**
     * 查询Published列表。
     * @return 结果列表
     */
    @Override
    public List<Announcement> listPublished() {
        return announcementMapper.selectList(new LambdaQueryWrapper<AnnouncementEntity>()
                        .eq(AnnouncementEntity::getStatus, AnnouncementStatus.PUBLISHED.code())
                        .orderByDesc(AnnouncementEntity::getPublishAt)
                        .orderByDesc(AnnouncementEntity::getId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 查找DueScheduled。
     *
     * @param now now
     * @return 查找结果
     */
    @Override
    public List<Announcement> findDueScheduled(LocalDateTime now) {
        LocalDateTime effective = now == null ? LocalDateTime.now() : now;
        return announcementMapper.selectList(new LambdaQueryWrapper<AnnouncementEntity>()
                        .eq(AnnouncementEntity::getStatus, AnnouncementStatus.DRAFT.code())
                        .isNotNull(AnnouncementEntity::getPublishAt)
                        .le(AnnouncementEntity::getPublishAt, effective))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * 执行publishDue。
     *
     * @param now now
     * @return 执行结果
     */
    @Override
    public int publishDue(LocalDateTime now) {
        List<Announcement> due = findDueScheduled(now);
        for (Announcement item : due) {
            AnnouncementEntity entity = require(item.id());
            entity.setStatus(AnnouncementStatus.PUBLISHED.code());
            announcementMapper.updateById(entity);
        }
        return due.size();
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public Announcement create(AnnouncementSaveRequest request) {
        AnnouncementEntity entity = map(request, new AnnouncementEntity());
        announcementMapper.insert(entity);
        return toDomain(entity);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    @Override
    public Announcement update(Long id, AnnouncementSaveRequest request) {
        AnnouncementEntity entity = map(request, require(id));
        announcementMapper.updateById(entity);
        return toDomain(entity);
    }

    /**
     * 删除Item。
     *
     * @param id 主键 ID
     */
    @Override
    public void delete(Long id) {
        require(id);
        announcementMapper.deleteById(id);
    }

    /**
     * 执行map。
     *
     * @param request 请求
     * @param entity 实体
     * @return 执行结果
     */
    private AnnouncementEntity map(AnnouncementSaveRequest request, AnnouncementEntity entity) {
        entity.setTitle(request.getTitle().trim());
        entity.setBody(request.getBody());
        entity.setStatus(StringUtils.hasText(request.getStatus())
                ? request.getStatus() : AnnouncementStatus.DRAFT.code());
        entity.setPublishAt(request.getPublishAt());
        entity.setTargetLevelCodes(announcementAudienceSupport.normalizeCodes(request.getTargetLevelCodes()));
        entity.setTargetUserIds(announcementAudienceSupport.normalizeUserIds(request.getTargetUserIds()));
        return entity;
    }

    /**
     * 执行require。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    private AnnouncementEntity require(Long id) {
        AnnouncementEntity entity = announcementMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private Announcement toDomain(AnnouncementEntity entity) {
        return new Announcement(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                entity.getStatus(),
                entity.getPublishAt(),
                entity.getTargetLevelCodes(),
                entity.getTargetUserIds()
        );
    }
}
