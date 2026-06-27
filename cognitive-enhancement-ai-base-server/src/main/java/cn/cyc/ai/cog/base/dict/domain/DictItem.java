package cn.cyc.ai.cog.base.dict.domain;

/**
 * 字典项业务对象。
 */
public record DictItem(
        Long id,
        Long tenantId,
        String bizCode,
        Long typeId,
        Long parentId,
        String value,
        String label,
        String enLabel,
        String remark,
        int sort,
        boolean enabled
) {
}
