package cn.cyc.ai.cog.runtime.api;

import java.util.List;

/**
 * Runtime 通用列表返回对象。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record RuntimeListResult<T>(int total,
                                   List<T> items,
                                   int page,
                                   int size,
                                   int totalPages,
                                   boolean hasNext) {

    /**
     * 构造兼容旧调用方的完整列表结果。
     *
     * @param total 总量
     * @param items 列表项
     */
    public RuntimeListResult(int total, List<T> items) {
        this(total, items, 1, items == null ? 0 : items.size(), total == 0 ? 0 : 1, false);
    }
}
