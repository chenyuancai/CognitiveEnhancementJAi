package cn.cyc.ai.cog.runtime.api;

import java.util.List;

/**
 * Runtime 通用列表返回对象。
 *
 * @param total      总量
 * @param items      当前页列表项
 * @param page       当前页码，从 1 开始
 * @param size       每页数量
 * @param totalPages 总页数
 * @param hasNext    是否存在下一页
 * @param <T>        列表项类型
 * @author cyc
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
