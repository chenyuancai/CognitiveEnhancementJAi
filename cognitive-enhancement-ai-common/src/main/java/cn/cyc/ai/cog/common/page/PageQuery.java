package cn.cyc.ai.cog.common.page;

/**
 * 通用分页查询参数基类（借鉴 zcloud Query）。
 *
 * <p>各模块的分页查询 DTO 继承本类，复用 page/size/排序约定。</p>
 *
 * @author cyc
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

    public long getCurrent() {
        return current < 1 ? 1 : current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size < 1 ? 10 : Math.min(size, 500);
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
