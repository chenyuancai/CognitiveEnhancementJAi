package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.api.LatestRuntimeRecordResult;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.service.RuntimeObservationQueryService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime 观测查询控制器。
 *
 * @author cyc
 */
@RestController
@RequestMapping("/api/runtime/observations")
public class RuntimeObservationController {

    /**
     * 控制器日志。
     */
    private static final Logger log = LoggerFactory.getLogger(RuntimeObservationController.class);

    /**
     * 观测查询服务。
     */
    private final RuntimeObservationQueryService runtimeObservationQueryService;

    /**
     * 构造 Runtime 观测控制器。
     *
     * @param runtimeObservationQueryService 观测查询服务
     */
    public RuntimeObservationController(RuntimeObservationQueryService runtimeObservationQueryService) {
        this.runtimeObservationQueryService = runtimeObservationQueryService;
    }

    /**
     * 查询执行记录列表。
     *
     * @param traceId        链路标识筛选条件
     * @param capabilityCode 能力编码筛选条件
     * @param agentCode      Agent 编码筛选条件
     * @return 执行记录列表
     */
    @GetMapping("/executions")
    public ApiResponse<RuntimeListResult<ExecutionRecord>> listExecutions(@RequestParam(name = "traceId", required = false) String traceId,
                                                                          @RequestParam(name = "capabilityCode", required = false) String capabilityCode,
                                                                          @RequestParam(name = "agentCode", required = false) String agentCode,
                                                                          @RequestParam(name = "page", defaultValue = "1") int page,
                                                                          @RequestParam(name = "size", defaultValue = "20") int size,
                                                                          @RequestParam(name = "sort", defaultValue = "recordedAt,desc") String sort) {
        log.info("收到执行记录查询请求, traceId={}, capabilityCode={}, agentCode={}, page={}, size={}, sort={}",
                traceId, capabilityCode, agentCode, page, size, sort);
        return RuntimeResponses.success(runtimeObservationQueryService.listExecutionRecords(traceId, capabilityCode, agentCode, page, size, sort));
    }

    /**
     * 查询用量记录列表。
     *
     * @param traceId        链路标识筛选条件
     * @param capabilityCode 能力编码筛选条件
     * @param agentCode      Agent 编码筛选条件
     * @return 用量记录列表
     */
    @GetMapping("/usages")
    public ApiResponse<RuntimeListResult<UsageRecord>> listUsages(@RequestParam(name = "traceId", required = false) String traceId,
                                                                  @RequestParam(name = "capabilityCode", required = false) String capabilityCode,
                                                                  @RequestParam(name = "agentCode", required = false) String agentCode,
                                                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                                                  @RequestParam(name = "size", defaultValue = "20") int size,
                                                                  @RequestParam(name = "sort", defaultValue = "recordedAt,desc") String sort) {
        log.info("收到用量记录查询请求, traceId={}, capabilityCode={}, agentCode={}, page={}, size={}, sort={}",
                traceId, capabilityCode, agentCode, page, size, sort);
        return RuntimeResponses.success(runtimeObservationQueryService.listUsageRecords(traceId, capabilityCode, agentCode, page, size, sort));
    }

    /**
     * 查询模型检查记录列表。
     *
     * @param traceId      链路标识筛选条件
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 模型检查记录列表
     */
    @GetMapping("/model-checks")
    public ApiResponse<RuntimeListResult<ModelCheckRecord>> listModelChecks(@RequestParam(name = "traceId", required = false) String traceId,
                                                                            @RequestParam(name = "providerCode", required = false) String providerCode,
                                                                            @RequestParam(name = "modelCode", required = false) String modelCode,
                                                                            @RequestParam(name = "page", defaultValue = "1") int page,
                                                                            @RequestParam(name = "size", defaultValue = "20") int size,
                                                                            @RequestParam(name = "sort", defaultValue = "recordedAt,desc") String sort) {
        log.info("收到模型检查记录查询请求, traceId={}, providerCode={}, modelCode={}, page={}, size={}, sort={}",
                traceId, providerCode, modelCode, page, size, sort);
        return RuntimeResponses.success(runtimeObservationQueryService.listModelCheckRecords(traceId, providerCode, modelCode, page, size, sort));
    }

    /**
     * 查询模型最近一次检查记录。
     *
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 最新模型检查记录
     */
    @GetMapping("/model-checks/latest")
    public ApiResponse<LatestRuntimeRecordResult<ModelCheckRecord>> getLatestModelCheck(@RequestParam(name = "providerCode", required = false) String providerCode,
                                                                                         @RequestParam(name = "modelCode", required = false) String modelCode) {
        log.info("收到模型最新检查记录查询请求, providerCode={}, modelCode={}", providerCode, modelCode);
        return RuntimeResponses.success(runtimeObservationQueryService.getLatestModelCheckRecord(providerCode, modelCode));
    }
}
