package cn.cyc.ai.cog.app.contract;

import cn.cyc.ai.cog.common.page.PageResult;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 契约分页响应（mock 使用 items/page，内部兼容 records/current）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPageVO<T> {

    /** 当前页数据 */
    private List<T> items = Collections.emptyList();

    /** 总条数 */
    private long total;

    /** 当前页码 */
    private long page;

    /** 每页条数 */
    private long size;

    /**
     * 从内部 {@link PageResult} 转换并映射元素类型。
     */
    public static <S, T> AppPageVO<T> from(PageResult<S> source, Function<S, T> mapper) {
        AppPageVO<T> vo = new AppPageVO<>();
        if (source == null) {
            vo.setPage(1);
            vo.setSize(20);
            return vo;
        }
        vo.setItems(source.getRecords() == null
                ? Collections.emptyList()
                : source.getRecords().stream().map(mapper).toList());
        vo.setTotal(source.getTotal());
        vo.setPage(source.getCurrent());
        vo.setSize(source.getSize());
        return vo;
    }

    /**
     * 直接构造契约分页结果。
     */
    public static <T> AppPageVO<T> of(List<T> items, long total, long page, long size) {
        AppPageVO<T> vo = new AppPageVO<>();
        vo.setItems(items == null ? Collections.emptyList() : items);
        vo.setTotal(total);
        vo.setPage(page);
        vo.setSize(size);
        return vo;
    }
}
