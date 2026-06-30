package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckRequest;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckResult;

/**
 * 模型连通性检查服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface ModelConnectivityCheckService {

    /**
     * 执行一次模型连通性检查。
     *
     * @param request 检查请求
     * @return 检查结果
     */
    ModelConnectivityCheckResult check(ModelConnectivityCheckRequest request);
}
