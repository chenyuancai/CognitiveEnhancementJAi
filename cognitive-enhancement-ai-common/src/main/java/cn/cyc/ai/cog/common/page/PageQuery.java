package cn.cyc.ai.cog.common.page;

/**
 * 通用分页查询参数基类（借鉴 zcloud Query）。
 * <p>各模块的分页查询 DTO 继承本类，复用 page/size/排序约定。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class PageQuery {

    /** 页码，从 1 开始。 */
    private long current = 1;

    /** 每页条数。 */
    private long size = 10;

    /** 排序字段（蛇形列名）。 */
    private String sortField;

    /** 是否升序。 */
    private boolean asc = false;

    /**
     * 获取Current。
     * @return Current
     */
    public long getCurrent() {
        return current < 1 ? 1 : current;
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
        return size < 1 ? 10 : Math.min(size, 500);
    }

    /**
     * 设置大小。
     *
     * @param size 大小
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * 获取SortField。
     * @return SortField
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * 设置SortField。
     *
     * @param sortField sortField
     */
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /**
     * 判断是否为Asc。
     * @return 是否满足条件
     */
    public boolean isAsc() {
        return asc;
    }

    /**
     * 设置Asc。
     *
     * @param asc asc
     */
    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
