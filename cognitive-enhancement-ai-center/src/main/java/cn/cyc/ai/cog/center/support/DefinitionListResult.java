package cn.cyc.ai.cog.center.support;

import java.util.List;

/**
 * 管理中心列表返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record DefinitionListResult<T>(
        int total,
        List<T> items
) {
}
