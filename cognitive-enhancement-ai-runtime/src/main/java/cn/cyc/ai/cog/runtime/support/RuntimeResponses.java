package cn.cyc.ai.cog.runtime.support;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.core.trace.TraceContext;

/**
 * Runtime 统一响应工厂。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class RuntimeResponses {

    /**
     * 禁止实例化。
     */
    private RuntimeResponses() {
    }

    /**
     * 构造成功响应。
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 标准成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data, TraceContext.getTraceId());
    }
}
