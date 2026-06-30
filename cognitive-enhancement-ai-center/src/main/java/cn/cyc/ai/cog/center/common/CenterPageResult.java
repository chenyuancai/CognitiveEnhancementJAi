package cn.cyc.ai.cog.center.common;

import java.util.List;

/**
 * Center 统一分页返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record CenterPageResult<T>(
        int total,
        List<T> items,
        int page,
        int size,
        int totalPages,
        boolean hasNext
) {

    public CenterPageResult {
        items = List.copyOf(items == null ? List.of() : items);
    }
}
