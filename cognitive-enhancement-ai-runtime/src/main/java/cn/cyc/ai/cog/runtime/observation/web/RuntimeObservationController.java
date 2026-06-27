package cn.cyc.ai.cog.runtime.observation.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.runtime.audit.domain.AuditLogRecord;
import cn.cyc.ai.cog.runtime.audit.service.AuditLogQueryService;
import cn.cyc.ai.cog.runtime.observation.dto.ExecutionRecordDetail;
import cn.cyc.ai.cog.runtime.observation.service.RuntimeObservationQueryService;
import cn.cyc.ai.cog.runtime.api.LatestRuntimeRecordResult;
import cn.cyc.ai.cog.runtime.observation.dto.ObservationStatsResult;
import cn.cyc.ai.cog.runtime.api.RuntimeListResult;
import cn.cyc.ai.cog.runtime.observation.domain.ExecutionRecord;
import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import cn.cyc.ai.cog.runtime.observation.domain.UsageRecord;
import cn.cyc.ai.cog.runtime.trace.domain.TraceSpan;
import cn.cyc.ai.cog.runtime.trace.service.TraceSpanQueryService;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * Runtime 观测查询控制器。
 *
 * @author cyc
 */
@Tag(name = "Runtime - 观测", description = "执行记录、用量、Trace Span、审计日志与统计聚合")
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
    private final TraceSpanQueryService traceSpanQueryService;
    private final AuditLogQueryService auditLogQueryService;

    public RuntimeObservationController(RuntimeObservationQueryService runtimeObservationQueryService,
                                        TraceSpanQueryService traceSpanQueryService,
                                        AuditLogQueryService auditLogQueryService) {
        this.runtimeObservationQueryService = runtimeObservationQueryService;
        this.traceSpanQueryService = traceSpanQueryService;
        this.auditLogQueryService = auditLogQueryService;
    }

    /**
     * 查询执行记录列表。
     *
     * @param traceId        链路标识筛选条件
     * @param capabilityCode 能力编码筛选条件
     * @param agentCode      Agent 编码筛选条件
     * @return 执行记录列表
     */
    @Operation(summary = "分页查询执行记录", description = "支持 traceId、capabilityCode、时间窗口与 sort 排序。")
    @GetMapping("/executions")
    public ApiResponse<RuntimeListResult<ExecutionRecord>> listExecutions(@RequestParam(name = "traceId", required = false) String traceId,
                                                                          @RequestParam(name = "capabilityCode", required = false) String capabilityCode,
                                                                          @RequestParam(name = "agentCode", required = false) String agentCode,
                                                                          @RequestParam(name = "startTime", required = false) Instant startTime,
                                                                          @RequestParam(name = "endTime", required = false) Instant endTime,
                                                                          @RequestParam(name = "page", defaultValue = "1") int page,
                                                                          @RequestParam(name = "size", defaultValue = "20") int size,
                                                                          @RequestParam(name = "sort", defaultValue = "recordedAt,desc") String sort) {
        log.info("收到执行记录查询请求, traceId={}, capabilityCode={}, agentCode={}, startTime={}, endTime={}, page={}, size={}, sort={}",
                traceId, capabilityCode, agentCode, startTime, endTime, page, size, sort);
        return RuntimeResponses.success(runtimeObservationQueryService.listExecutionRecords(
                traceId, capabilityCode, agentCode, startTime, endTime, page, size, sort));
    }

    /**
     * 按 traceId 查询单次执行链路详情。
     *
     * @param traceId 链路标识
     * @return 执行链路详情
     */
    @Operation(summary = "查询执行链路详情", description = "按 traceId 返回 input/routing/result 及关联 usages。")
    @GetMapping("/executions/{traceId}")
    public ApiResponse<ExecutionRecordDetail> getExecutionDetail(@PathVariable String traceId) {
        log.info("收到执行链路详情查询请求, traceId={}", traceId);
        return RuntimeResponses.success(runtimeObservationQueryService.getExecutionRecordDetail(traceId));
    }

    /**
     * 查询用量记录列表。
     *
     * @param traceId        链路标识筛选条件
     * @param capabilityCode 能力编码筛选条件
     * @param agentCode      Agent 编码筛选条件
     * @return 用量记录列表
     */
    @Operation(summary = "分页查询用量记录", description = "支持 traceId、capabilityCode、时间窗口筛选。")
    @GetMapping("/usages")
    public ApiResponse<RuntimeListResult<UsageRecord>> listUsages(@RequestParam(name = "traceId", required = false) String traceId,
                                                                  @RequestParam(name = "capabilityCode", required = false) String capabilityCode,
                                                                  @RequestParam(name = "agentCode", required = false) String agentCode,
                                                                  @RequestParam(name = "startTime", required = false) Instant startTime,
                                                                  @RequestParam(name = "endTime", required = false) Instant endTime,
                                                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                                                  @RequestParam(name = "size", defaultValue = "20") int size,
                                                                  @RequestParam(name = "sort", defaultValue = "recordedAt,desc") String sort) {
        log.info("收到用量记录查询请求, traceId={}, capabilityCode={}, agentCode={}, startTime={}, endTime={}, page={}, size={}, sort={}",
                traceId, capabilityCode, agentCode, startTime, endTime, page, size, sort);
        return RuntimeResponses.success(runtimeObservationQueryService.listUsageRecords(
                traceId, capabilityCode, agentCode, startTime, endTime, page, size, sort));
    }

    /**
     * 查询模型检查记录列表。
     *
     * @param traceId      链路标识筛选条件
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 模型检查记录列表
     */
    @Operation(summary = "分页查询模型检查记录", description = "查询历史模型连通性检查记录。")
    @GetMapping("/model-checks")
    public ApiResponse<RuntimeListResult<ModelCheckRecord>> listModelChecks(@RequestParam(name = "traceId", required = false) String traceId,
                                                                            @RequestParam(name = "providerCode", required = false) String providerCode,
                                                                            @RequestParam(name = "modelCode", required = false) String modelCode,
                                                                            @RequestParam(name = "startTime", required = false) Instant startTime,
                                                                            @RequestParam(name = "endTime", required = false) Instant endTime,
                                                                            @RequestParam(name = "page", defaultValue = "1") int page,
                                                                            @RequestParam(name = "size", defaultValue = "20") int size,
                                                                            @RequestParam(name = "sort", defaultValue = "recordedAt,desc") String sort) {
        log.info("收到模型检查记录查询请求, traceId={}, providerCode={}, modelCode={}, startTime={}, endTime={}, page={}, size={}, sort={}",
                traceId, providerCode, modelCode, startTime, endTime, page, size, sort);
        return RuntimeResponses.success(runtimeObservationQueryService.listModelCheckRecords(
                traceId, providerCode, modelCode, startTime, endTime, page, size, sort));
    }

    /**
     * 查询模型最近一次检查记录。
     *
     * @param providerCode 模型提供方筛选条件
     * @param modelCode    模型编码筛选条件
     * @return 最新模型检查记录
     */
    @Operation(summary = "查询最近模型检查", description = "返回最新一条模型检查记录。")
    @GetMapping("/model-checks/latest")
    public ApiResponse<LatestRuntimeRecordResult<ModelCheckRecord>> getLatestModelCheck(@RequestParam(name = "providerCode", required = false) String providerCode,
                                                                                         @RequestParam(name = "modelCode", required = false) String modelCode) {
        log.info("收到模型最新检查记录查询请求, providerCode={}, modelCode={}", providerCode, modelCode);
        return RuntimeResponses.success(runtimeObservationQueryService.getLatestModelCheckRecord(providerCode, modelCode));
    }

    /**
     * 聚合观测统计（按能力 / 模型 / Tool）。
     *
     * @param startTime 起始时间（含）
     * @param endTime   结束时间（含）
     * @return 聚合统计结果
     */
    @Operation(summary = "聚合观测统计", description = "按能力/模型/Tool 维度聚合调用统计。")
    @GetMapping("/stats")
    public ApiResponse<ObservationStatsResult> aggregateStats(
            @RequestParam(name = "startTime", required = false) Instant startTime,
            @RequestParam(name = "endTime", required = false) Instant endTime) {
        log.info("收到观测聚合统计请求, startTime={}, endTime={}", startTime, endTime);
        return RuntimeResponses.success(runtimeObservationQueryService.aggregateStats(startTime, endTime));
    }

    /**
     * 按 traceId 查询 Trace Span 步骤树。
     */
    @Operation(summary = "查询 Trace Span 步骤树", description = "按 traceId 返回 AGENT/TOOL/LLM 等步骤树。")
    @GetMapping("/traces/{traceId}/spans")
    public ApiResponse<RuntimeListResult<TraceSpan>> listTraceSpans(@PathVariable String traceId) {
        log.info("收到 Trace Span 查询请求, traceId={}", traceId);
        List<TraceSpan> spans = traceSpanQueryService.listByTraceId(traceId);
        return RuntimeResponses.success(new RuntimeListResult<>(spans.size(), spans));
    }

    /**
     * 分页查询审计日志。
     */
    @Operation(summary = "分页查询审计日志", description = "查询配置变更与运行时审计事件。")
    @GetMapping("/audit-logs")
    public ApiResponse<RuntimeListResult<AuditLogRecord>> listAuditLogs(
            @RequestParam(name = "traceId", required = false) String traceId,
            @RequestParam(name = "eventType", required = false) String eventType,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "resourceType", required = false) String resourceType,
            @RequestParam(name = "resourceCode", required = false) String resourceCode,
            @RequestParam(name = "success", required = false) Boolean success,
            @RequestParam(name = "startTime", required = false) Instant startTime,
            @RequestParam(name = "endTime", required = false) Instant endTime,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "recordedAt,desc") String sort) {
        log.info("收到审计日志查询请求, traceId={}, eventType={}, action={}", traceId, eventType, action);
        return RuntimeResponses.success(auditLogQueryService.listAuditLogs(
                traceId, eventType, action, resourceType, resourceCode, success,
                startTime, endTime, page, size, sort));
    }
}
