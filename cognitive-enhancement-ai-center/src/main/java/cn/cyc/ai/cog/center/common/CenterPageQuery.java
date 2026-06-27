package cn.cyc.ai.cog.center.common;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * Center 统一分页查询参数。
 *
 * @author cyc
 */
public class CenterPageQuery {

    /**
     * 页码，从 1 开始。
     */
    private int page = 1;

    /**
     * 每页条数。
     */
    private int size = 20;

    /**
     * 排序表达式，格式 field,asc|desc。
     */
    private String sort;

    /**
     * 编码或名称模糊匹配关键字。
     */
    private String keyword;

    /**
     * 启用状态。
     */
    private CommonStatus status;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }
}
