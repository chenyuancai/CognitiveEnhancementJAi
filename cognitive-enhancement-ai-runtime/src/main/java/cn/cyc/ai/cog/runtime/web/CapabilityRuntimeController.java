package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteRequest;
import cn.cyc.ai.cog.runtime.api.CapabilityExecuteResponse;
import cn.cyc.ai.cog.runtime.spi.CapabilityRuntime;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 能力入口控制器。
 *
 * @author cyc
 */
@RestController
@RequestMapping("/api/runtime/capabilities")
public class CapabilityRuntimeController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(CapabilityRuntimeController.class);

    /**
     * 能力运行时入口。
     */
    private final CapabilityRuntime capabilityRuntime;

    /**
     * 构造能力运行时控制器。
     *
     * @param capabilityRuntime 能力运行时入口
     */
    public CapabilityRuntimeController(CapabilityRuntime capabilityRuntime) {
        this.capabilityRuntime = capabilityRuntime;
    }

    /**
     * 执行能力主链路。
     *
     * @param request 执行请求
     * @return 执行响应
     */
    @PostMapping("/execute")
    public ApiResponse<CapabilityExecuteResponse> execute(@RequestBody CapabilityExecuteRequest request) {
        log.info("收到 Runtime API 请求, capabilityCode={}", request.capabilityCode());
        return RuntimeResponses.success(capabilityRuntime.execute(request));
    }
}
