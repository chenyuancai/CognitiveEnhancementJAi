package cn.cyc.ai.cog.center.common;

import java.util.List;

/**
 * Center 统一分页返回对象。
 *
 * @param total      总记录数
 * @param items      当前页数据
 * @param page       当前页码，从 1 开始
 * @param size       每页条数
 * @param totalPages 总页数
 * @param hasNext    是否存在下一页
 * @param <T>        数据类型
 * @author cyc
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
