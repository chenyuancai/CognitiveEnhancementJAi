package cn.cyc.ai.cog.center.common;

import java.util.List;

/**
 * Center 列表响应体。
 *
 * @param items 列表项
 * @param total 总数
 * @param <T>   列表项类型
 */
public record ListResponse<T>(
        List<T> items,
        int total
) {
}
