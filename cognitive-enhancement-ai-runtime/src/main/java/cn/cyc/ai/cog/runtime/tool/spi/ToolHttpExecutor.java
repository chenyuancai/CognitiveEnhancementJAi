package cn.cyc.ai.cog.runtime.tool.spi;

import cn.cyc.ai.cog.runtime.api.ToolHttpRequest;
import cn.cyc.ai.cog.runtime.api.ToolHttpResponse;

/**
 * HTTP Tool 执行 SPI。
 *
 * @author cyc
 */
public interface ToolHttpExecutor {

    /**
     * 执行 HTTP Tool 请求。
     *
     * @param request HTTP 请求
     * @return HTTP 响应
     */
    ToolHttpResponse execute(ToolHttpRequest request);
}
