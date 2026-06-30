package cn.cyc.ai.cog.center.common;

import cn.cyc.ai.cog.core.metadata.type.CommonStatus;

/**
 * Center 统一分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 获取分页。
     * @return 分页
     */
    public int getPage() {
        return page;
    }

    /**
     * 设置分页。
     *
     * @param page 分页
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * 获取大小。
     * @return 大小
     */
    public int getSize() {
        return size;
    }

    /**
     * 设置大小。
     *
     * @param size 大小
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 获取Sort。
     * @return Sort
     */
    public String getSort() {
        return sort;
    }

    /**
     * 设置Sort。
     *
     * @param sort sort
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * 获取关键词。
     * @return 关键词
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * 设置关键词。
     *
     * @param keyword 关键词
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 获取状态。
     * @return 状态
     */
    public CommonStatus getStatus() {
        return status;
    }

    /**
     * 设置状态。
     *
     * @param status 状态
     */
    public void setStatus(CommonStatus status) {
        this.status = status;
    }
}
