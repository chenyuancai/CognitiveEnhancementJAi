package cn.cyc.ai.cog.center.support;

import java.util.List;

/**
 * 管理中心列表返回对象。
 *
 * @param total 总量
 * @param items 列表项
 * @param <T>   项类型
 * @author cyc
 */
public record DefinitionListResult<T>(
        int total,
        List<T> items
) {
}
