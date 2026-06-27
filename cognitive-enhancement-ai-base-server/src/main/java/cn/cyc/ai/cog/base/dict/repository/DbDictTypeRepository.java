package cn.cyc.ai.cog.base.dict.repository;

import cn.cyc.ai.cog.base.config.BaseServiceProperties;
import cn.cyc.ai.cog.base.dict.domain.DictType;
import cn.cyc.ai.cog.base.dict.dto.DictTypePageQuery;
import cn.cyc.ai.cog.base.dict.dto.DictTypeSaveRequest;
import cn.cyc.ai.cog.base.dict.entity.DictTypeEntity;
import cn.cyc.ai.cog.base.dict.mapper.DictTypeMapper;
import cn.cyc.ai.cog.base.dict.support.DictConverter;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 字典类型 MyBatis 仓储实现。
 */
@Repository
public class DbDictTypeRepository implements DictTypeRepository {

    private final DictTypeMapper dictTypeMapper;
    private final DictItemRepository dictItemRepository;
    private final BaseServiceProperties baseServiceProperties;

    public DbDictTypeRepository(DictTypeMapper dictTypeMapper,
                                @Lazy DictItemRepository dictItemRepository,
                                BaseServiceProperties baseServiceProperties) {
        this.dictTypeMapper = dictTypeMapper;
        this.dictItemRepository = dictItemRepository;
        this.baseServiceProperties = baseServiceProperties;
    }

    @Override
    public DictType save(int dictKind, DictTypeSaveRequest request) {
        String bizCode = resolveBizCode(request.getBizCode());
        String shareScope = resolveShareScope(request.getShareScope());
        Long tenantId = request.getTenantId() == null ? 1L : request.getTenantId();
        assertCodeUnique(dictKind, bizCode, shareScope, tenantId, request.getCode(), request.getId());

        DictTypeEntity entity = request.getId() == null ? new DictTypeEntity() : requireEntity(request.getId());
        entity.setBizCode(bizCode);
        entity.setDictKind(dictKind);
        entity.setShareScope(shareScope);
        entity.setTenantId(tenantId);
        entity.setCode(request.getCode().trim());
        entity.setName(request.getName().trim());
        entity.setEnName(StringUtils.hasText(request.getEnName()) ? request.getEnName().trim() : request.getCode().trim());
        entity.setDescription(request.getDescription());
        entity.setRemark(request.getRemark());
        entity.setStatus(Boolean.FALSE.equals(request.getStatus()) ? 0 : 1);

        if (request.getId() == null) {
            dictTypeMapper.insert(entity);
        } else {
            dictTypeMapper.updateById(entity);
        }
        return DictConverter.toType(entity);
    }

    @Override
    public void deleteById(Long id) {
        requireEntity(id);
        dictItemRepository.deleteByTypeId(id);
        dictTypeMapper.deleteById(id);
    }

    @Override
    public Optional<DictType> findById(Long id) {
        DictTypeEntity entity = dictTypeMapper.selectById(id);
        return Optional.ofNullable(entity).map(DictConverter::toType);
    }

    @Override
    public Optional<DictType> findByCode(int dictKind, String bizCode, String shareScope, Long tenantId, String code) {
        DictTypeEntity entity = dictTypeMapper.selectOne(new LambdaQueryWrapper<DictTypeEntity>()
                .eq(DictTypeEntity::getDictKind, dictKind)
                .eq(DictTypeEntity::getBizCode, bizCode)
                .eq(DictTypeEntity::getShareScope, shareScope)
                .eq(DictTypeEntity::getTenantId, tenantId)
                .eq(DictTypeEntity::getCode, code));
        return Optional.ofNullable(entity).map(DictConverter::toType);
    }

    @Override
    public PageResult<DictType> page(int dictKind, DictTypePageQuery query) {
        String bizCode = resolveBizCode(query.getBizCode());
        LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<DictTypeEntity>()
                .eq(DictTypeEntity::getDictKind, dictKind)
                .eq(DictTypeEntity::getBizCode, bizCode)
                .orderByAsc(DictTypeEntity::getCode);
        if (StringUtils.hasText(query.getShareScope())) {
            wrapper.eq(DictTypeEntity::getShareScope, query.getShareScope());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(DictTypeEntity::getCode, query.getKeyword())
                    .or().like(DictTypeEntity::getName, query.getKeyword()));
        }
        Page<DictTypeEntity> page = dictTypeMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        List<DictType> records = page.getRecords().stream().map(DictConverter::toType).toList();
        return PageResult.of(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<DictType> listByCode(int dictKind, String code) {
        LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<DictTypeEntity>()
                .eq(DictTypeEntity::getDictKind, dictKind)
                .eq(DictTypeEntity::getBizCode, baseServiceProperties.getDefaultBizCode())
                .orderByAsc(DictTypeEntity::getCode);
        if (StringUtils.hasText(code)) {
            wrapper.eq(DictTypeEntity::getCode, code);
        }
        return dictTypeMapper.selectList(wrapper).stream().map(DictConverter::toType).toList();
    }

    private void assertCodeUnique(int dictKind, String bizCode, String shareScope, Long tenantId,
                                  String code, Long excludeId) {
        LambdaQueryWrapper<DictTypeEntity> wrapper = new LambdaQueryWrapper<DictTypeEntity>()
                .eq(DictTypeEntity::getDictKind, dictKind)
                .eq(DictTypeEntity::getBizCode, bizCode)
                .eq(DictTypeEntity::getShareScope, shareScope)
                .eq(DictTypeEntity::getTenantId, tenantId)
                .eq(DictTypeEntity::getCode, code.trim());
        if (excludeId != null) {
            wrapper.ne(DictTypeEntity::getId, excludeId);
        }
        Errors.throwIf(dictTypeMapper.selectCount(wrapper) > 0, PlatformErrorCode.DICT_TYPE_CODE_DUPLICATE);
    }

    private DictTypeEntity requireEntity(Long id) {
        DictTypeEntity entity = dictTypeMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.DICT_TYPE_NOT_FOUND);
        }
        return entity;
    }

    private String resolveBizCode(String bizCode) {
        return StringUtils.hasText(bizCode) ? bizCode.trim() : baseServiceProperties.getDefaultBizCode();
    }

    private String resolveShareScope(String shareScope) {
        return StringUtils.hasText(shareScope) ? shareScope.trim() : baseServiceProperties.getDefaultShareScope();
    }
}
