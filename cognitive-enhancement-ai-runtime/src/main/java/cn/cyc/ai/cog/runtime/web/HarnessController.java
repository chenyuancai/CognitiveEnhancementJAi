package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessReportSummary;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessReportQuery;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessRunRequest;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessRunResponse;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenarioTemplate;
import cn.cyc.ai.cog.runtime.harness.support.HarnessImportWorkflowSupport;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessEngine;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessReportRepository;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Harness 测试控制器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "Admin - Harness", description = "Agent Harness 治理验证：异步演练、报告查询与场景模板")
@RestController
@RequestMapping("/api/admin/harness")
public class HarnessController {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(HarnessController.class);

    /** harnessEngine。 */
    private final HarnessEngine harnessEngine;
    /** harnessSteps。 */
    private final List<HarnessStep> harnessSteps;
    /** report仓储。 */
    private final HarnessReportRepository reportRepository;

    /**
     * 创建Harness接口。
     */
    public HarnessController(HarnessEngine harnessEngine,
                              List<HarnessStep> harnessSteps,
                              HarnessReportRepository reportRepository) {
        this.harnessEngine = harnessEngine;
        this.harnessSteps = harnessSteps;
        this.reportRepository = reportRepository;
    }

    /**
     * 执行操作。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "启动 Harness 演练", description = "异步执行预置治理步骤链，立即返回 harnessId。")
    @PostMapping("/run")
    public ApiResponse<HarnessRunResponse> run(@RequestBody HarnessRunRequest request) {
        String harnessId = "HAR-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 6);
        String traceId = TraceContext.getTraceId();
        if (traceId == null || traceId.isBlank()) {
            traceId = harnessId;
        }

        HarnessContext context = new HarnessContext(
                harnessId, traceId, Instant.now(),
                request.scenario(), null, null, null, null, null, Map.of()
        );

        log.info("收到 Harness 执行请求, harnessId={}, traceId={}", harnessId, traceId);

        CompletableFuture.runAsync(() -> {
            HarnessReport report = harnessEngine.run(harnessSteps, context);
            reportRepository.save(report);
        }).exceptionally(ex -> {
            log.error("Harness 异步执行失败, harnessId={}", harnessId, ex);
            return null;
        });

        return RuntimeResponses.success(new HarnessRunResponse(harnessId, "RUNNING", "Harness 执行中"));
    }

    /**
     * 获取Report。
     *
     * @param harnessId harnessID
     * @return Report
     */
    @Operation(summary = "查询 Harness 报告", description = "按 harnessId 查询完整演练报告。")
    @GetMapping("/reports/{harnessId}")
    public ApiResponse<HarnessReport> getReport(@PathVariable("harnessId") String harnessId) {
        return reportRepository.findById(harnessId)
                .map(RuntimeResponses::success)
                .orElse(RuntimeResponses.success(null));
    }

    /**
     * 查询Reports列表。
     * @return 结果列表
     */
    @Operation(summary = "分页查询 Harness 报告", description = "支持 status、startFrom、startTo 筛选。")
    @GetMapping("/reports")
    public ApiResponse<Page<HarnessReportSummary>> listReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant startFrom,
            @RequestParam(required = false) Instant startTo) {
        HarnessReportQuery query = new HarnessReportQuery(status, startFrom, startTo);
        Page<HarnessReport> reportPage = reportRepository.findPage(new Page<>(page, size), query);
        List<HarnessReportSummary> summaries = reportPage.getRecords().stream()
                .map(r -> new HarnessReportSummary(
                        r.harnessId(), r.status(), r.startTime(), r.totalDurationMs(),
                        r.scenario() != null ? r.scenario().capabilityName() : ""
                ))
                .toList();
        Page<HarnessReportSummary> resultPage = new Page<>(reportPage.getCurrent(), reportPage.getSize(), reportPage.getTotal());
        resultPage.setRecords(summaries);
        return RuntimeResponses.success(resultPage);
    }

    /**
     * 获取LatestReport。
     * @return LatestReport
     */
    @Operation(summary = "查询最新 Harness 报告", description = "返回最近一条演练报告。")
    @GetMapping("/reports/latest")
    public ApiResponse<HarnessReport> getLatestReport() {
        return reportRepository.findLatest()
                .map(RuntimeResponses::success)
                .orElse(RuntimeResponses.success(null));
    }

    /**
     * 获取ScenarioTemplates。
     * @return ScenarioTemplates
     */
    @Operation(summary = "查询 Harness 场景模板", description = "返回内置 QA/Chat 等演练场景模板。")
    @GetMapping("/scenario-templates")
    public ApiResponse<List<HarnessScenarioTemplate>> getScenarioTemplates() {
        List<HarnessScenarioTemplate> templates = List.of(
                new HarnessScenarioTemplate(
                        "智能问答",
                        "使用问答 Agent 进行事实性问答",
                        new HarnessScenario(
                                "capability.qa.answer", "agent.qa",
                                List.of("skill.qa"), List.of("tool.search"),
                                "qwen-plus", Map.of("question", "Harness 测试问题")
                        )
                ),
                new HarnessScenarioTemplate(
                        "智能对话",
                        "使用对话 Agent 进行通用对话",
                        new HarnessScenario(
                                "capability.chat.generate", "agent.chat",
                                List.of("skill.chat"), List.of(),
                                "qwen-plus", Map.of("question", "你好")
                        )
                ),
                new HarnessScenarioTemplate(
                        "知识文件导入",
                        "调试 KB 文件解析导入流水线（inputParams 需提供 fileId）",
                        new HarnessScenario(
                                "capability.kb.summary", "agent.qa",
                                List.of(), List.of(),
                                "qwen-plus",
                                Map.of(
                                        HarnessImportWorkflowSupport.WORKFLOW_TYPE_KEY,
                                        HarnessImportWorkflowSupport.IMPORT_KB_FILE_PARSE,
                                        "importBizType", "KNOWLEDGE_DOCUMENT",
                                        "fileId", 1,
                                        "title", "Harness 导入测试",
                                        "tenantId", 1,
                                        "aiEnhanced", false
                                )
                        )
                )
        );
        return RuntimeResponses.success(templates);
    }
}
