package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;

/**
 * 能力运行时入口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface CapabilityRuntime {

    /**
     * 按能力编码执行一次运行时路由。
     *
     * @param request 执行请求
     * @return 执行响应
     */
    CapabilityExecuteResponse execute(CapabilityExecuteRequest request);
}
