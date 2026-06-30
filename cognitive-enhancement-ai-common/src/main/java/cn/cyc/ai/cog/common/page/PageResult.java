package cn.cyc.ai.cog.common.page;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用分页结果（借鉴 zcloud IPage 包装）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class PageResult<T> {

    /** records。 */
    private List<T> records;
    /** 总数。 */
    private long total;
    /** current。 */
    private long current;
    /** 大小。 */
    private long size;

    /**
     * 创建分页结果。
     */
    public PageResult() {
        this.records = Collections.emptyList();
    }

    /**
     * 创建分页结果。
     *
     * @param records records
     * @param total 总数
     * @param current current
     * @param size 大小
     */
    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records == null ? Collections.emptyList() : records;
        this.total = total;
        this.current = current;
        this.size = size;
    }

    /**
     * 执行of。
     *
     * @param records records
     * @param total 总数
     * @param current current
     * @param size 大小
     * @return 执行结果
     */
    public static <T> PageResult<T> of(List<T> records, long total, long current, long size) {
        return new PageResult<>(records, total, current, size);
    }

    /**
     * 执行empty。
     *
     * @param current current
     * @param size 大小
     * @return 执行结果
     */
    public static <T> PageResult<T> empty(long current, long size) {
        return new PageResult<>(Collections.emptyList(), 0, current, size);
    }

    /** 将记录类型映射为另一种类型，保留分页元信息。 */
    public <R> PageResult<R> map(Function<T, R> mapper) {
        List<R> mapped = records.stream().map(mapper).collect(Collectors.toList());
        return new PageResult<>(mapped, total, current, size);
    }

    /**
     * 获取Records。
     * @return Records
     */
    public List<T> getRecords() {
        return records;
    }

    /**
     * 设置Records。
     *
     * @param records records
     */
    public void setRecords(List<T> records) {
        this.records = records;
    }

    /**
     * 获取总数。
     * @return 总数
     */
    public long getTotal() {
        return total;
    }

    /**
     * 设置总数。
     *
     * @param total 总数
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * 获取Current。
     * @return Current
     */
    public long getCurrent() {
        return current;
    }

    /**
     * 设置Current。
     *
     * @param current current
     */
    public void setCurrent(long current) {
        this.current = current;
    }

    /**
     * 获取大小。
     * @return 大小
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置大小。
     *
     * @param size 大小
     */
    public void setSize(long size) {
        this.size = size;
    }
}
