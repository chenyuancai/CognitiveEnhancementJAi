package cn.cyc.ai.cog.center.common;

import java.util.List;

/**
 * Center 列表响应体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ListResponse<T>(
        List<T> items,
        int total
) {
}
