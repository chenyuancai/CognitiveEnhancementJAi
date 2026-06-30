package cn.cyc.ai.cog.runtime.spi;

import cn.cyc.ai.cog.runtime.api.LlmHttpRequest;
import cn.cyc.ai.cog.runtime.api.LlmHttpResponse;

/**
 * LLM HTTP 执行器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface LlmHttpExecutor {

    /**
     * 执行一次 HTTP 请求。
     *
     * @param request HTTP 请求
     * @return HTTP 响应
     */
    LlmHttpResponse execute(LlmHttpRequest request);
}
