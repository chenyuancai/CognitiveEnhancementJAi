package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.Content;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentTag;
import cn.cyc.ai.cog.platform.knowledge.domain.ContentVersion;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentAuditRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentPageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentTagBindRequest;
import cn.cyc.ai.cog.platform.knowledge.entity.ContentEntity;
import cn.cyc.ai.cog.api.enums.ContentStatus;
import cn.cyc.ai.cog.platform.knowledge.entity.ContentTagEntity;
import cn.cyc.ai.cog.platform.knowledge.entity.ContentTagRelEntity;
import cn.cyc.ai.cog.platform.knowledge.mapper.ContentMapper;
import cn.cyc.ai.cog.platform.knowledge.mapper.ContentTagMapper;
import cn.cyc.ai.cog.platform.knowledge.mapper.ContentTagRelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbContentRepository implements ContentRepository {

    /** 内容 Mapper */
    private final ContentMapper contentMapper;

    /** 内容标签关联 Mapper */
    private final ContentTagRelMapper contentTagRelMapper;

    /** 内容标签 Mapper */
    private final ContentTagMapper contentTagMapper;

    /** 内容版本仓储 */
    private final ContentVersionRepository contentVersionRepository;

    /**
     * @param contentMapper        内容 Mapper
     * @param contentTagRelMapper  内容标签关联 Mapper
     * @param contentTagMapper     内容标签 Mapper
     * @param contentVersionRepository 内容版本仓储
     */
    public DbContentRepository(ContentMapper contentMapper,
                               ContentTagRelMapper contentTagRelMapper,
                               ContentTagMapper contentTagMapper,
                               ContentVersionRepository contentVersionRepository) {
        this.contentMapper = contentMapper;
        this.contentTagRelMapper = contentTagRelMapper;
        this.contentTagMapper = contentTagMapper;
        this.contentVersionRepository = contentVersionRepository;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<Content> page(ContentPageQuery query) {
        LambdaQueryWrapper<ContentEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(ContentEntity::getTitle, query.getKeyword());
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(ContentEntity::getContentType, query.getType());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(ContentEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(ContentEntity::getId);
        wrapper.select(ContentEntity.class, info -> !"body".equals(info.getColumn()));
        Page<ContentEntity> page = contentMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
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
    public Content findById(Long id) {
        return toDomain(require(id));
    }

    /**
     * 查找Tags人内容ID。
     *
     * @param contentId 内容ID
     * @return 查找结果
     */
    @Override
    public List<ContentTag> findTagsByContentId(Long contentId) {
        require(contentId);
        List<ContentTagRelEntity> rels = contentTagRelMapper.selectList(new LambdaQueryWrapper<ContentTagRelEntity>()
                .eq(ContentTagRelEntity::getContentId, contentId));
        if (rels.isEmpty()) {
            return List.of();
        }
        List<Long> tagIds = rels.stream().map(ContentTagRelEntity::getTagId).toList();
        return contentTagMapper.selectBatchIds(tagIds).stream().map(this::toTagDomain).toList();
    }

    /**
     * 执行bindTags。
     *
     * @param contentId 内容ID
     * @param request 请求
     * @return 执行结果
     */
    @Override
    @Transactional
    public List<ContentTag> bindTags(Long contentId, ContentTagBindRequest request) {
        require(contentId);
        contentTagRelMapper.delete(new LambdaQueryWrapper<ContentTagRelEntity>()
                .eq(ContentTagRelEntity::getContentId, contentId));
        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            return List.of();
        }
        for (Long tagId : request.getTagIds()) {
            ContentTagEntity tag = contentTagMapper.selectById(tagId);
            if (tag == null) {
                throw Errors.of(PlatformErrorCode.CONTENT_TAG_NOT_FOUND, "标签不存在：" + tagId);
            }
            ContentTagRelEntity rel = new ContentTagRelEntity();
            rel.setContentId(contentId);
            rel.setTagId(tagId);
            contentTagRelMapper.insert(rel);
        }
        return findTagsByContentId(contentId);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public Content create(ContentSaveRequest request) {
        ContentEntity content = new ContentEntity();
        applySave(request, content);
        content.setStatus(ContentStatus.PENDING.code());
        contentMapper.insert(content);
        return toDomain(content);
    }

    /**
     * 创建ImportDraft。
     *
     * @param tenantId 租户 ID
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public Content createImportDraft(Long tenantId, ContentSaveRequest request) {
        ContentEntity content = new ContentEntity();
        content.setTenantId(tenantId == null ? 1L : tenantId);
        applySave(request, content);
        content.setStatus(ContentStatus.DRAFT.code());
        contentMapper.insert(content);
        return toDomain(content);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    @Override
    public Content update(Long id, ContentSaveRequest request) {
        ContentEntity content = require(id);
        applySave(request, content);
        contentMapper.updateById(content);
        return toDomain(content);
    }

    /**
     * 执行audit。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 执行结果
     */
    @Override
    @Transactional
    public Content audit(Long id, ContentAuditRequest request) {
        ContentEntity content = require(id);
        if (!ContentStatus.PENDING.matches(content.getStatus())) {
            throw Errors.of(PlatformErrorCode.CONTENT_AUDIT_STATUS_INVALID, "仅待审核内容可审核，当前状态：" + content.getStatus());
        }
        content.setAuditRemark(request.getRemark());
        if (Boolean.TRUE.equals(request.getPass())) {
            int nextVersion = content.getCurrentVersion() == null ? 1 : content.getCurrentVersion() + 1;
            content.setStatus(ContentStatus.PUBLISHED.code());
            content.setCurrentVersion(nextVersion);
            content.setPublishedAt(LocalDateTime.now());
            contentMapper.updateById(content);
            contentVersionRepository.append(new ContentVersion(
                    null,
                    content.getId(),
                    nextVersion,
                    content.getTitle(),
                    content.getSummary(),
                    content.getBody(),
                    content.getMinLevelCode(),
                    UserContext.currentUserId(),
                    LocalDateTime.now()
            ));
        } else {
            content.setStatus(ContentStatus.REJECTED.code());
            contentMapper.updateById(content);
        }
        return toDomain(content);
    }

    /**
     * 查询Versions列表。
     *
     * @param contentId 内容ID
     * @return 结果列表
     */
    @Override
    public List<ContentVersion> listVersions(Long contentId) {
        require(contentId);
        return contentVersionRepository.listByContentId(contentId);
    }

    /**
     * 执行rollbackTo版本号。
     *
     * @param contentId 内容ID
     * @param versionNo 版本号，每次更新递增
     * @return 执行结果
     */
    @Override
    @Transactional
    public Content rollbackToVersion(Long contentId, int versionNo) {
        ContentEntity content = require(contentId);
        ContentVersion snapshot = contentVersionRepository.find(contentId, versionNo)
                .orElseThrow(() -> Errors.of(PlatformErrorCode.CONTENT_VERSION_NOT_FOUND, "版本不存在：" + versionNo));
        content.setTitle(snapshot.title());
        content.setSummary(snapshot.summary());
        content.setBody(snapshot.body());
        content.setMinLevelCode(snapshot.minLevelCode());
        content.setStatus(ContentStatus.DRAFT.code());
        content.setAuditRemark(null);
        contentMapper.updateById(content);
        return toDomain(content);
    }

    /**
     * 执行offline。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Override
    public Content offline(Long id) {
        ContentEntity content = require(id);
        if (!ContentStatus.PUBLISHED.matches(content.getStatus())) {
            throw Errors.of(PlatformErrorCode.CONTENT_OFFLINE_STATUS_INVALID, "仅已发布内容可下线，当前状态：" + content.getStatus());
        }
        content.setStatus(ContentStatus.OFFLINE.code());
        contentMapper.updateById(content);
        return toDomain(content);
    }

    /**
     * 执行数量人租户。
     *
     * @param tenantId 租户 ID
     * @return 执行结果
     */
    @Override
    public long countByTenant(Long tenantId) {
        LambdaQueryWrapper<ContentEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(ContentEntity::getTenantId, tenantId);
        }
        return contentMapper.selectCount(wrapper);
    }

    /**
     * 执行数量人状态。
     *
     * @param tenantId 租户 ID
     * @param status 状态
     * @return 执行结果
     */
    @Override
    public long countByStatus(Long tenantId, String status) {
        LambdaQueryWrapper<ContentEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(ContentEntity::getTenantId, tenantId);
        }
        wrapper.eq(ContentEntity::getStatus, status);
        return contentMapper.selectCount(wrapper);
    }

    /**
     * 执行数量人状态And更新时间Between。
     * @return 执行结果
     */
    @Override
    public long countByStatusAndUpdateTimeBetween(Long tenantId, String status,
                                                  java.time.LocalDateTime start, java.time.LocalDateTime end) {
        LambdaQueryWrapper<ContentEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(ContentEntity::getTenantId, tenantId);
        }
        wrapper.eq(ContentEntity::getStatus, status);
        wrapper.ge(ContentEntity::getUpdateTime, start);
        wrapper.le(ContentEntity::getUpdateTime, end);
        return contentMapper.selectCount(wrapper);
    }

    /**
     * 执行applySave。
     *
     * @param request 请求
     * @param content 内容
     */
    private void applySave(ContentSaveRequest request, ContentEntity content) {
        content.setTitle(request.getTitle());
        content.setContentType(request.getType());
        content.setAuthor(request.getAuthor());
        content.setSummary(request.getSummary());
        content.setBody(request.getBody());
        content.setMinLevelCode(request.getMinLevelCode());
    }

    /**
     * 执行require。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    private ContentEntity require(Long id) {
        ContentEntity content = contentMapper.selectById(id);
        if (content == null) {
            throw Errors.of(PlatformErrorCode.CONTENT_NOT_FOUND, "内容不存在：" + id);
        }
        return content;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private Content toDomain(ContentEntity entity) {
        return new Content(
                entity.getId(),
                entity.getTitle(),
                entity.getContentType(),
                entity.getAuthor(),
                entity.getStatus(),
                entity.getSummary(),
                entity.getBody(),
                entity.getAuditRemark(),
                entity.getMinLevelCode(),
                entity.getCurrentVersion(),
                entity.getPublishedAt()
        );
    }

    /**
     * 转换为标签Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private ContentTag toTagDomain(ContentTagEntity entity) {
        return new ContentTag(entity.getId(), entity.getTagName(), entity.getTagColor());
    }
}
