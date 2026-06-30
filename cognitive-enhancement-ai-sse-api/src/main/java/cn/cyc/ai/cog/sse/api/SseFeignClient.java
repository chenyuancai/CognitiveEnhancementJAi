package cn.cyc.ai.cog.sse.api;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.sse.api.model.SseSendRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SSE 服务内部推送客户端（供 app-server / admin-server 等调用）。
 * <p>默认直连 {@code cog.sse.url}，生产部署 SSE 单副本时指向该实例。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@FeignClient(
        name = SseConstants.SERVICE_NAME,
        url = "${cog.sse.url:http://localhost:8806}",
        path = SseConstants.CLIENT_API_PREFIX
)
public interface SseFeignClient {

    @PostMapping("/send")
    ApiResponse<Boolean> send(@Valid @RequestBody SseSendRequest request);
}
