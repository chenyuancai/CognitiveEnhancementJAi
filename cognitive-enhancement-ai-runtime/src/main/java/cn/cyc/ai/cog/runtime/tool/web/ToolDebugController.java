package cn.cyc.ai.cog.runtime.tool.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import cn.cyc.ai.cog.runtime.tool.dto.ToolDebugInvokeRequest;
import cn.cyc.ai.cog.runtime.tool.dto.ToolDebugInvokeResponse;
import cn.cyc.ai.cog.runtime.tool.service.ToolDebugService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tool 调试调用控制器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Runtime - Tool 调试", description = "Tool 管理页试调用入口，绕过完整 Capability 链路")
@RestController
@RequestMapping("/api/runtime/tools")
public class ToolDebugController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ToolDebugController.class);

    /**
     * Tool 调试服务。
     */
    private final ToolDebugService toolDebugService;

    /**
     * 构造 Tool 调试控制器。
     *
     * @param toolDebugService Tool 调试服务
     */
    public ToolDebugController(ToolDebugService toolDebugService) {
        this.toolDebugService = toolDebugService;
    }

    /**
     * 调试调用单个 Tool。
     *
     * @param toolCode             Tool 编码
     * @param request              调试请求
     * @param headerDebugConfirmed 请求头确认标识
     * @return 调试调用响应
     */
    @Operation(summary = "调试调用 Tool", description = "按 toolCode 直接试调用 Tool，返回 invocationResult。用于 Tool 配置页验真。")
    @PostMapping("/debug-invoke")
    public ApiResponse<ToolDebugInvokeResponse> debugInvoke(
            @RequestBody(required = false) ToolDebugInvokeRequest request,
            @RequestHeader(value = "X-Debug-Confirmed", required = false) Boolean headerDebugConfirmed) {
        ToolDebugInvokeRequest body = request == null ? new ToolDebugInvokeRequest(null, null, null, null, null) : request;
        log.info("收到 Tool 调试调用请求, toolCode={}", body.toolCode());
        return RuntimeResponses.success(toolDebugService.debugInvoke(body.toolCode(), body, headerDebugConfirmed));
    }
}
