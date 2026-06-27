package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckRequest;
import cn.cyc.ai.cog.runtime.api.ModelConnectivityCheckResult;
import cn.cyc.ai.cog.runtime.api.ModelGovernanceStateResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusOverviewResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusSummaryResult;
import cn.cyc.ai.cog.runtime.api.ModelStatusRefreshRequest;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.model.governance.DefaultModelGovernance;
import cn.cyc.ai.cog.runtime.service.ModelRuntimeQueryService;
import cn.cyc.ai.cog.runtime.spi.ModelConnectivityCheckService;
import cn.cyc.ai.cog.runtime.spi.ModelStatusRefreshService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Runtime 模型连通性检查控制器。
 *
 * @author cyc
 */
@Tag(name = "Runtime - 模型", description = "模型连通性检查、状态总览与熔断治理查询")
@RestController
@RequestMapping("/api/runtime/models")
public class ModelConnectivityController {

    private static final Logger log = LoggerFactory.getLogger(ModelConnectivityController.class);

    private final ModelConnectivityCheckService modelConnectivityCheckService;
    private final ModelRuntimeQueryService modelRuntimeQueryService;
    private final ModelStatusRefreshService modelStatusRefreshService;
    private final DefaultModelGovernance modelGovernance;

    public ModelConnectivityController(ModelConnectivityCheckService modelConnectivityCheckService,
                                       ModelRuntimeQueryService modelRuntimeQueryService,
                                       ModelStatusRefreshService modelStatusRefreshService,
                                       DefaultModelGovernance modelGovernance) {
        this.modelConnectivityCheckService = modelConnectivityCheckService;
        this.modelRuntimeQueryService = modelRuntimeQueryService;
        this.modelStatusRefreshService = modelStatusRefreshService;
        this.modelGovernance = modelGovernance;
    }

    @Operation(summary = "检查模型连通性", description = "对指定 modelCode 发起连通性探测并返回检查结果。")
    @PostMapping("/check")
    public ApiResponse<ModelConnectivityCheckResult> check(@RequestBody ModelConnectivityCheckRequest request) {
        log.info("收到模型连通性检查请求, modelCode={}", request == null ? null : request.modelCode());
        return RuntimeResponses.success(modelConnectivityCheckService.check(request));
    }

    @Operation(summary = "查询模型状态列表", description = "按 providerCode/modelCode 筛选模型运行状态摘要。")
    @GetMapping("/statuses")
    public ApiResponse<RuntimeListResult<ModelStatusSummaryResult>> listStatuses(
            @RequestParam(name = "providerCode", required = false) String providerCode,
            @RequestParam(name = "modelCode", required = false) String modelCode) {
        log.info("收到模型状态摘要查询请求, providerCode={}, modelCode={}", providerCode, modelCode);
        return RuntimeResponses.success(modelRuntimeQueryService.listModelStatuses(providerCode, modelCode));
    }

    @Operation(summary = "查询模型状态总览", description = "聚合成功/失败次数、最近检查时间与失败摘要。")
    @GetMapping("/overview")
    public ApiResponse<ModelStatusOverviewResult> getOverview() {
        log.info("收到模型状态总览查询请求");
        return RuntimeResponses.success(modelRuntimeQueryService.getModelStatusOverview());
    }

    @Operation(summary = "查询模型治理状态", description = "返回熔断状态、连续失败次数与降级模型信息。")
    @GetMapping("/governance")
    public ApiResponse<RuntimeListResult<ModelGovernanceStateResult>> listGovernanceStates() {
        log.info("收到模型治理状态查询请求");
        List<ModelGovernanceStateResult> items = modelGovernance.listGovernanceStates();
        return RuntimeResponses.success(new RuntimeListResult<>(items.size(), items));
    }

    @Operation(summary = "刷新模型状态", description = "批量重新检查模型连通性并更新状态。")
    @PostMapping("/statuses/refresh")
    public ApiResponse<RuntimeListResult<ModelConnectivityCheckResult>> refreshStatuses(
            @RequestBody ModelStatusRefreshRequest request) {
        log.info("收到模型状态刷新请求, modelCode={}, modelCodeCount={}",
                request == null ? null : request.modelCode(),
                request == null || request.modelCodes() == null ? 0 : request.modelCodes().size());
        return RuntimeResponses.success(modelStatusRefreshService.refresh(request));
    }
}
