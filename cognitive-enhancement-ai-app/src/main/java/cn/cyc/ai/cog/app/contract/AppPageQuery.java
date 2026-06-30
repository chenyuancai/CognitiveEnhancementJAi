package cn.cyc.ai.cog.app.contract;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * 契约分页请求（兼容 mock {@code page} 与内部 {@code current}）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Data
public class AppPageQuery {

    /** 页码（兼容 current 别名） */
    @JsonAlias("current")
    private long page = 1;

    /** 每页条数 */
    private long size = 20;

    /** 关键词（可选） */
    private String keyword;

    /** 解析页码，最小为 1 */
    public long resolvePage() {
        return page < 1 ? 1 : page;
    }

    /** 解析每页条数，范围 1~500 */
    public long resolveSize() {
        return size < 1 ? 20 : Math.min(size, 500);
    }
}
