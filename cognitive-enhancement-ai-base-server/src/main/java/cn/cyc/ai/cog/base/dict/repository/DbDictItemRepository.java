package cn.cyc.ai.cog.base.dict.repository;

import cn.cyc.ai.cog.base.config.BaseServiceProperties;
import cn.cyc.ai.cog.base.dict.domain.DictItem;
import cn.cyc.ai.cog.base.dict.dto.DictItemSaveRequest;
import cn.cyc.ai.cog.base.dict.entity.DictItemEntity;
import cn.cyc.ai.cog.base.dict.entity.DictTypeEntity;
import cn.cyc.ai.cog.base.dict.enums.DictKindEnum;
import cn.cyc.ai.cog.base.dict.mapper.DictItemMapper;
import cn.cyc.ai.cog.base.dict.mapper.DictTypeMapper;
import cn.cyc.ai.cog.base.dict.support.DictConverter;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 字典项 MyBatis 仓储实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbDictItemRepository implements DictItemRepository {

    /** dictItemMapper。 */
    private final DictItemMapper dictItemMapper;
    /** dict类型Mapper。 */
    private final DictTypeMapper dictTypeMapper;
    /** base服务Properties。 */
    private final BaseServiceProperties baseServiceProperties;

    /**
     * 创建DbDictItem仓储。
     */
    public DbDictItemRepository(DictItemMapper dictItemMapper,
                                DictTypeMapper dictTypeMapper,
                                BaseServiceProperties baseServiceProperties) {
        this.dictItemMapper = dictItemMapper;
        this.dictTypeMapper = dictTypeMapper;
        this.baseServiceProperties = baseServiceProperties;
    }

    /**
     * 执行save。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Override
    public DictItem save(DictItemSaveRequest request) {
        DictTypeEntity typeEntity = requireType(request.getTypeId());
        Long tenantId = request.getTenantId() == null ? typeEntity.getTenantId() : request.getTenantId();
        String bizCode = request.getBizCode() == null ? typeEntity.getBizCode() : request.getBizCode();
        Long parentId = request.getParentId() == null ? 0L : request.getParentId();
        assertValueUnique(tenantId, request.getTypeId(), request.getValue(), request.getId());

        DictItemEntity entity = request.getId() == null ? new DictItemEntity() : requireEntity(request.getId());
        entity.setBizCode(bizCode);
        entity.setTenantId(tenantId);
        entity.setTypeId(request.getTypeId());
        entity.setParentId(parentId);
        entity.setValue(request.getValue().trim());
        entity.setLabel(request.getLabel().trim());
        entity.setEnLabel(StringUtils.hasText(request.getEnLabel()) ? request.getEnLabel().trim() : request.getLabel().trim());
        entity.setRemark(request.getRemark());
        entity.setSort(request.getSort() == null ? 0 : request.getSort());
        entity.setStatus(Boolean.FALSE.equals(request.getStatus()) ? 0 : 1);

        if (request.getId() == null) {
            dictItemMapper.insert(entity);
        } else {
            dictItemMapper.updateById(entity);
        }
        return DictConverter.toItem(entity);
    }

    /**
     * 删除人ID。
     *
     * @param id 主键 ID
     */
    @Override
    public void deleteById(Long id) {
        requireEntity(id);
        dictItemMapper.deleteById(id);
    }

    /**
     * 删除人类型ID。
     *
     * @param typeId 类型ID
     */
    @Override
    public void deleteByTypeId(Long typeId) {
        dictItemMapper.delete(new LambdaQueryWrapper<DictItemEntity>().eq(DictItemEntity::getTypeId, typeId));
    }

    /**
     * 查找人ID。
     *
     * @param id 主键 ID
     * @return 查找结果
     */
    @Override
    public Optional<DictItem> findById(Long id) {
        DictItemEntity entity = dictItemMapper.selectById(id);
        return Optional.ofNullable(entity).map(DictConverter::toItem);
    }

    /**
     * 查询人类型ID列表。
     *
     * @param typeId 类型ID
     * @return 结果列表
     */
    @Override
    public List<DictItem> listByTypeId(Long typeId) {
        return dictItemMapper.selectList(new LambdaQueryWrapper<DictItemEntity>()
                        .eq(DictItemEntity::getTypeId, typeId)
                        .orderByAsc(DictItemEntity::getSort)
                        .orderByAsc(DictItemEntity::getId))
                .stream().map(DictConverter::toItem).toList();
    }

    /**
     * 查询是否启用人类型编码列表。
     *
     * @param code 编码
     * @return 结果列表
     */
    @Override
    public List<DictItem> listEnabledByTypeCode(String code) {
        DictTypeEntity type = dictTypeMapper.selectOne(new LambdaQueryWrapper<DictTypeEntity>()
                .eq(DictTypeEntity::getDictKind, DictKindEnum.DICT.getValue())
                .eq(DictTypeEntity::getBizCode, baseServiceProperties.getDefaultBizCode())
                .eq(DictTypeEntity::getCode, code));
        if (type == null) {
            throw Errors.of(PlatformErrorCode.DICT_TYPE_NOT_FOUND, "字典类型不存在：" + code);
        }
        return dictItemMapper.selectList(new LambdaQueryWrapper<DictItemEntity>()
                        .eq(DictItemEntity::getTypeId, type.getId())
                        .eq(DictItemEntity::getStatus, 1)
                        .orderByAsc(DictItemEntity::getSort)
                        .orderByAsc(DictItemEntity::getId))
                .stream().map(DictConverter::toItem).toList();
    }

    /**
     * 执行assert值Unique。
     *
     * @param tenantId 租户 ID
     * @param typeId 类型ID
     * @param value 值
     * @param excludeId excludeID
     */
    private void assertValueUnique(Long tenantId, Long typeId, String value, Long excludeId) {
        LambdaQueryWrapper<DictItemEntity> wrapper = new LambdaQueryWrapper<DictItemEntity>()
                .eq(DictItemEntity::getTenantId, tenantId)
                .eq(DictItemEntity::getTypeId, typeId)
                .eq(DictItemEntity::getValue, value.trim());
        if (excludeId != null) {
            wrapper.ne(DictItemEntity::getId, excludeId);
        }
        Errors.throwIf(dictItemMapper.selectCount(wrapper) > 0, PlatformErrorCode.DICT_ITEM_VALUE_DUPLICATE);
    }

    /**
     * 执行require实体。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    private DictItemEntity requireEntity(Long id) {
        DictItemEntity entity = dictItemMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.DICT_ITEM_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 执行require类型。
     *
     * @param typeId 类型ID
     * @return 执行结果
     */
    private DictTypeEntity requireType(Long typeId) {
        DictTypeEntity entity = dictTypeMapper.selectById(typeId);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.DICT_TYPE_NOT_FOUND);
        }
        return entity;
    }
}
