package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckRequest;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusOverviewResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusSummaryResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusRefreshRequest;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.service.ModelRuntimeQueryService;
import cn.cyc.ai.cog.runtime.spi.ModelConnectivityCheckService;
import cn.cyc.ai.cog.runtime.spi.ModelStatusRefreshService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 模型连通性检查控制器。
 *
 * @author cyc
 */
@RestController
@RequestMapping("/api/runtime/models")
public class ModelConnectivityController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(ModelConnectivityController.class);

    /**
     * 模型检查服务。
     */
    private final ModelConnectivityCheckService modelConnectivityCheckService;

    /**
     * 模型查询服务。
     */
    private final ModelRuntimeQueryService modelRuntimeQueryService;

    /**
     * 模型状态刷新服务。
     */
    private final ModelStatusRefreshService modelStatusRefreshService;

    /**
     * 构造模型检查控制器。
     *
     * @param modelConnectivityCheckService 模型检查服务
     * @param modelRuntimeQueryService      模型查询服务
     * @param modelStatusRefreshService     模型状态刷新服务
     */
    public ModelConnectivityController(ModelConnectivityCheckService modelConnectivityCheckService,
                                       ModelRuntimeQueryService modelRuntimeQueryService,
                                       ModelStatusRefreshService modelStatusRefreshService) {
        this.modelConnectivityCheckService = modelConnectivityCheckService;
        this.modelRuntimeQueryService = modelRuntimeQueryService;
        this.modelStatusRefreshService = modelStatusRefreshService;
    }

    /**
     * 检查模型连通性。
     *
     * @param request 检查请求
     * @return 检查结果
     */
    @PostMapping("/check")
    public ApiResponse<ModelConnectivityCheckResult> check(@RequestBody ModelConnectivityCheckRequest request) {
        log.info("收到模型连通性检查请求, modelCode={}", request == null ? null : request.modelCode());
        return RuntimeResponses.success(modelConnectivityCheckService.check(request));
    }

    /**
     * 查询模型状态摘要列表。
     *
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 模型状态摘要列表
     */
    @GetMapping("/statuses")
    public ApiResponse<RuntimeListResult<ModelStatusSummaryResult>> listStatuses(@RequestParam(name = "providerCode", required = false) String providerCode,
                                                                                 @RequestParam(name = "modelCode", required = false) String modelCode) {
        log.info("收到模型状态摘要查询请求, providerCode={}, modelCode={}", providerCode, modelCode);
        return RuntimeResponses.success(modelRuntimeQueryService.listModelStatuses(providerCode, modelCode));
    }

    /**
     * 查询模型状态总览。
     *
     * @return 模型状态总览
     */
    @GetMapping("/overview")
    public ApiResponse<ModelStatusOverviewResult> getOverview() {
        log.info("收到模型状态总览查询请求");
        return RuntimeResponses.success(modelRuntimeQueryService.getModelStatusOverview());
    }

    /**
     * 刷新模型状态。
     *
     * @param request 刷新请求
     * @return 刷新结果列表
     */
    @PostMapping("/statuses/refresh")
    public ApiResponse<RuntimeListResult<ModelConnectivityCheckResult>> refreshStatuses(@RequestBody ModelStatusRefreshRequest request) {
        log.info("收到模型状态刷新请求, modelCode={}, modelCodeCount={}",
                request == null ? null : request.modelCode(),
                request == null || request.modelCodes() == null ? 0 : request.modelCodes().size());
        return RuntimeResponses.success(modelStatusRefreshService.refresh(request));
    }
}
