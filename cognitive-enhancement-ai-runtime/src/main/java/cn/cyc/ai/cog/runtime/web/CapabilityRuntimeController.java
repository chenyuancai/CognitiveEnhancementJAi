package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
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
     * 运行时治理器。
     */
    private final RuntimeHarness runtimeHarness;

    /**
     * 构造能力运行时控制器。
     *
     * @param runtimeHarness 运行时治理器
     */
    public CapabilityRuntimeController(RuntimeHarness runtimeHarness) {
        this.runtimeHarness = runtimeHarness;
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
        return RuntimeResponses.success(runtimeHarness.execute(request));
    }
}
