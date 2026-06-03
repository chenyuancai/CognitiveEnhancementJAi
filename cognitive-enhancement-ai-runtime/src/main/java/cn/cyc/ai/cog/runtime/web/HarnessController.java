package cn.cyc.ai.cog.runtime.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessReportSummary;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessRunRequest;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessRunResponse;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenarioTemplate;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessEngine;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessReportRepository;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.support.RuntimeResponses;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 */
@RestController
@RequestMapping("/api/admin/harness")
public class HarnessController {

    private static final Logger log = LoggerFactory.getLogger(HarnessController.class);

    private final HarnessEngine harnessEngine;
    private final List<HarnessStep> harnessSteps;
    private final HarnessReportRepository reportRepository;

    public HarnessController(HarnessEngine harnessEngine,
                              List<HarnessStep> harnessSteps,
                              HarnessReportRepository reportRepository) {
        this.harnessEngine = harnessEngine;
        this.harnessSteps = harnessSteps;
        this.reportRepository = reportRepository;
    }

    @PostMapping("/run")
    public ApiResponse<HarnessRunResponse> run(@RequestBody HarnessRunRequest request) {
        String harnessId = "HAR-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 6);
        String traceId = TraceContext.getTraceId();

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

    @GetMapping("/reports/{harnessId}")
    public ApiResponse<HarnessReport> getReport(@PathVariable("harnessId") String harnessId) {
        return reportRepository.findById(harnessId)
                .map(RuntimeResponses::success)
                .orElse(RuntimeResponses.success(null));
    }

    @GetMapping("/reports")
    public ApiResponse<Page<HarnessReportSummary>> listReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<HarnessReport> reportPage = reportRepository.findPage(new Page<>(page, size));
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

    @GetMapping("/reports/latest")
    public ApiResponse<HarnessReport> getLatestReport() {
        return reportRepository.findLatest()
                .map(RuntimeResponses::success)
                .orElse(RuntimeResponses.success(null));
    }

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
                )
        );
        return RuntimeResponses.success(templates);
    }
}
