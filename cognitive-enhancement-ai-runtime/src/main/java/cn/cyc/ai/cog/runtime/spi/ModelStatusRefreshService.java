package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusRefreshRequest;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;

/**
 * 模型状态刷新服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ModelStatusRefreshService {

    /**
     * 刷新一个或多个模型状态。
     *
     * @param request 刷新请求
     * @return 刷新结果列表
     */
    RuntimeListResult<ModelConnectivityCheckResult> refresh(ModelStatusRefreshRequest request);
}
