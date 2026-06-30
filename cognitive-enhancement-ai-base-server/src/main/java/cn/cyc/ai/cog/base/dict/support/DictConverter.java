package cn.cyc.ai.cog.base.dict.support;

import cn.cyc.ai.cog.base.dict.domain.DictItem;
import cn.cyc.ai.cog.base.dict.domain.DictType;
import cn.cyc.ai.cog.base.dict.dto.DictItemReadVO;
import cn.cyc.ai.cog.base.dict.dto.DictItemVO;
import cn.cyc.ai.cog.base.dict.dto.DictTypeTreeVO;
import cn.cyc.ai.cog.base.dict.dto.DictTypeVO;
import cn.cyc.ai.cog.base.dict.entity.DictItemEntity;
import cn.cyc.ai.cog.base.dict.entity.DictTypeEntity;

/**
 * 字典对象转换。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class DictConverter {

    /**
     * 创建DictConverter。
     */
    private DictConverter() {
    }

    /**
     * 转换为类型。
     *
     * @param entity 实体
     * @return 转换结果
     */
    public static DictType toType(DictTypeEntity entity) {
        return new DictType(
                entity.getId(),
                entity.getTenantId(),
                entity.getBizCode(),
                entity.getDictKind() == null ? 1 : entity.getDictKind(),
                entity.getShareScope(),
                entity.getCode(),
                entity.getName(),
                entity.getEnName(),
                entity.getDescription(),
                entity.getRemark(),
                entity.getStatus() != null && entity.getStatus() == 1
        );
    }

    /**
     * 转换为Item。
     *
     * @param entity 实体
     * @return 转换结果
     */
    public static DictItem toItem(DictItemEntity entity) {
        return new DictItem(
                entity.getId(),
                entity.getTenantId(),
                entity.getBizCode(),
                entity.getTypeId(),
                entity.getParentId(),
                entity.getValue(),
                entity.getLabel(),
                entity.getEnLabel(),
                entity.getRemark(),
                entity.getSort() == null ? 0 : entity.getSort(),
                entity.getStatus() != null && entity.getStatus() == 1
        );
    }

    /**
     * 转换为类型Vo。
     *
     * @param type 类型
     * @return 转换结果
     */
    public static DictTypeVO toTypeVo(DictType type) {
        DictTypeVO vo = new DictTypeVO();
        vo.setId(type.id());
        vo.setTenantId(type.tenantId());
        vo.setBizCode(type.bizCode());
        vo.setDictKind(type.dictKind());
        vo.setShareScope(type.shareScope());
        vo.setCode(type.code());
        vo.setName(type.name());
        vo.setEnName(type.enName());
        vo.setDescription(type.description());
        vo.setRemark(type.remark());
        vo.setStatus(type.enabled());
        return vo;
    }

    /**
     * 转换为ItemVo。
     *
     * @param item item
     * @return 转换结果
     */
    public static DictItemVO toItemVo(DictItem item) {
        DictItemVO vo = new DictItemVO();
        vo.setId(item.id());
        vo.setBizCode(item.bizCode());
        vo.setTypeId(item.typeId());
        vo.setParentId(item.parentId());
        vo.setValue(item.value());
        vo.setLabel(item.label());
        vo.setEnLabel(item.enLabel());
        vo.setRemark(item.remark());
        vo.setSort(item.sort());
        vo.setStatus(item.enabled());
        return vo;
    }

    /**
     * 转换为类型TreeVo。
     *
     * @param type 类型
     * @return 转换结果
     */
    public static DictTypeTreeVO toTypeTreeVo(DictType type) {
        DictTypeTreeVO vo = new DictTypeTreeVO();
        vo.setId(type.id());
        vo.setBizCode(type.bizCode());
        vo.setShareScope(type.shareScope());
        vo.setCode(type.code());
        vo.setName(type.name());
        vo.setEnName(type.enName());
        vo.setDescription(type.description());
        vo.setRemark(type.remark());
        vo.setStatus(type.enabled());
        return vo;
    }

    /**
     * 转换为ReadVo。
     *
     * @param item item
     * @return 转换结果
     */
    public static DictItemReadVO toReadVo(DictItem item) {
        DictItemReadVO vo = new DictItemReadVO();
        vo.setValue(item.value());
        vo.setLabel(item.label());
        vo.setEnLabel(item.enLabel());
        vo.setSort(item.sort());
        return vo;
    }
}
