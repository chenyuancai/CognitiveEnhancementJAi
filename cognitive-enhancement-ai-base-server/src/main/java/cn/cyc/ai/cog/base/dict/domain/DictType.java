package cn.cyc.ai.cog.base.dict.domain;

/**
 * 字典类型业务对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record DictType(
        Long id,
        Long tenantId,
        String bizCode,
        int dictKind,
        String shareScope,
        String code,
        String name,
        String enName,
        String description,
        String remark,
        boolean enabled
) {
}
