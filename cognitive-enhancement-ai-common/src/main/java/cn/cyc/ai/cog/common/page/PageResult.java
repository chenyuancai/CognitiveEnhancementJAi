package cn.cyc.ai.cog.common.page;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用分页结果（借鉴 zcloud IPage 包装）。
 *
 * @param <T> 行数据类型
 * @author cyc
 */
public class PageResult<T> {

    private List<T> records;
    private long total;
    private long current;
    private long size;

    public PageResult() {
        this.records = Collections.emptyList();
    }

    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records == null ? Collections.emptyList() : records;
        this.total = total;
        this.current = current;
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        return new PageResult<>(records, total, current, size);
    }

    public static <T> PageResult<T> empty(long current, long size) {
        return new PageResult<>(Collections.emptyList(), 0, current, size);
    }

    /** 将记录类型映射为另一种类型，保留分页元信息。 */
    public <R> PageResult<R> map(Function<T, R> mapper) {
        List<R> mapped = records.stream().map(mapper).collect(Collectors.toList());
        return new PageResult<>(mapped, total, current, size);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
