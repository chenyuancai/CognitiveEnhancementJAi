package cn.cyc.ai.cog.center.support;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.core.trace.TraceContext;

/**
 * 管理中心统一响应工厂，负责补齐链路追踪标识。
 *
 * @author cyc
 */
public final class CenterResponses {

    private CenterResponses() {
    }

    /**
     * 构造成功响应并透传当前追踪标识。
     *
     * @param data 响应数据
     * @param <T>  响应类型
     * @return 统一成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data, TraceContext.getTraceId());
    }
}
