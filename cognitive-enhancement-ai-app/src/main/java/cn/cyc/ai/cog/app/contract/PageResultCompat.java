package cn.cyc.ai.cog.app.contract;

import cn.cyc.ai.cog.common.page.PageQuery;

/**
 * 契约分页与内部 PageQuery 互转。
 *
 * @author cyc
 * @date 2026/6/29
 */
public final class PageResultCompat {

    private PageResultCompat() {
    }

    /**
     * 契约分页请求 → 内部 PageQuery。
     */
    public static PageQuery toPageQuery(AppPageQuery query) {
        AppPageQuery body = query == null ? new AppPageQuery() : query;
        PageQuery pageQuery = new PageQuery();
        pageQuery.setCurrent(body.resolvePage());
        pageQuery.setSize(body.resolveSize());
        pageQuery.setSortField(null);
        return pageQuery;
    }
}
