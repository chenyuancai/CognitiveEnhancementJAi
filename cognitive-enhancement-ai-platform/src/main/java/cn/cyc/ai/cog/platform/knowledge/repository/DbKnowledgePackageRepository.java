package cn.cyc.ai.cog.platform.knowledge.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackage;
import cn.cyc.ai.cog.platform.knowledge.domain.KnowledgePackageItem;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageItemSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackagePageQuery;
import cn.cyc.ai.cog.platform.knowledge.dto.KnowledgePackageSaveRequest;
import cn.cyc.ai.cog.platform.knowledge.entity.KnowledgePackageEntity;
import cn.cyc.ai.cog.platform.knowledge.entity.KnowledgePackageItemEntity;
import cn.cyc.ai.cog.platform.knowledge.mapper.KnowledgePackageItemMapper;
import cn.cyc.ai.cog.platform.knowledge.mapper.KnowledgePackageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 知识包仓储 MyBatis 实现。
 */
@Repository
public class DbKnowledgePackageRepository implements KnowledgePackageRepository {

    /** 知识包 Mapper */
    private final KnowledgePackageMapper packageMapper;

    /** 知识包条目 Mapper */
    private final KnowledgePackageItemMapper itemMapper;

    /**
     * @param packageMapper 知识包 Mapper
     * @param itemMapper    知识包条目 Mapper
     */
    public DbKnowledgePackageRepository(KnowledgePackageMapper packageMapper,
                                        KnowledgePackageItemMapper itemMapper) {
        this.packageMapper = packageMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    public PageResult<KnowledgePackage> page(KnowledgePackagePageQuery query) {
        LambdaQueryWrapper<KnowledgePackageEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(KnowledgePackageEntity::getPackageName, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(KnowledgePackageEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(KnowledgePackageEntity::getId);
        Page<KnowledgePackageEntity> page = packageMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public KnowledgePackage findById(Long id) {
        return toDomain(requirePackage(id));
    }

    @Override
    public List<KnowledgePackageItem> listItems(Long packageId) {
        requirePackage(packageId);
        return itemMapper.selectList(new LambdaQueryWrapper<KnowledgePackageItemEntity>()
                        .eq(KnowledgePackageItemEntity::getPackageId, packageId)
                        .orderByAsc(KnowledgePackageItemEntity::getSortNo))
                .stream().map(this::toItemDomain).toList();
    }

    @Override
    public KnowledgePackage create(KnowledgePackageSaveRequest request) {
        KnowledgePackageEntity entity = map(request, new KnowledgePackageEntity());
        packageMapper.insert(entity);
        return toDomain(entity);
    }

    @Override
    public KnowledgePackage update(Long id, KnowledgePackageSaveRequest request) {
        KnowledgePackageEntity entity = map(request, requirePackage(id));
        packageMapper.updateById(entity);
        return toDomain(entity);
    }

    @Override
    public KnowledgePackageItem addItem(Long packageId, KnowledgePackageItemSaveRequest request) {
        requirePackage(packageId);
        KnowledgePackageItemEntity item = new KnowledgePackageItemEntity();
        item.setPackageId(packageId);
        item.setParentId(request.getParentId() == null ? 0L : request.getParentId());
        item.setContentId(request.getContentId());
        item.setTitle(request.getTitle());
        item.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        itemMapper.insert(item);
        return toItemDomain(item);
    }

    @Override
    public void deleteItem(Long packageId, Long itemId) {
        requirePackage(packageId);
        KnowledgePackageItemEntity item = itemMapper.selectById(itemId);
        if (item == null || !packageId.equals(item.getPackageId())) {
            throw Errors.of(PlatformErrorCode.KNOWLEDGE_PACKAGE_ITEM_NOT_FOUND);
        }
        itemMapper.deleteById(itemId);
    }

    @Override
    public java.util.List<KnowledgePackage> listEnabled(Long tenantId) {
        LambdaQueryWrapper<KnowledgePackageEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(KnowledgePackageEntity::getTenantId, tenantId);
        }
        wrapper.eq(KnowledgePackageEntity::getStatus, CommonConstants.STATUS_ENABLED);
        wrapper.orderByDesc(KnowledgePackageEntity::getId);
        return packageMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    private KnowledgePackageEntity requirePackage(Long id) {
        KnowledgePackageEntity entity = packageMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.KNOWLEDGE_PACKAGE_NOT_FOUND);
        }
        return entity;
    }

    private KnowledgePackageEntity map(KnowledgePackageSaveRequest request, KnowledgePackageEntity entity) {
        entity.setPackageName(request.getPackageName().trim());
        entity.setDescription(request.getDescription());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : CommonConstants.STATUS_ENABLED);
        return entity;
    }

    private KnowledgePackage toDomain(KnowledgePackageEntity entity) {
        return new KnowledgePackage(entity.getId(), entity.getPackageName(), entity.getDescription(), entity.getStatus());
    }

    private KnowledgePackageItem toItemDomain(KnowledgePackageItemEntity entity) {
        return new KnowledgePackageItem(
                entity.getId(),
                entity.getPackageId(),
                entity.getParentId(),
                entity.getContentId(),
                entity.getTitle(),
                entity.getSortNo()
        );
    }
}
