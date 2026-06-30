package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.knowledge.process.ImportBizType;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import cn.cyc.ai.cog.runtime.harness.support.HarnessImportWorkflowSupport;
import cn.cyc.ai.cog.runtime.importkb.ImportWorkflowRunner;
import cn.cyc.ai.cog.platform.file.spi.PlatformFileClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Harness 步骤：知识文件导入流水线（IMPORT_KB_FILE_PARSE）。
 */
@Component
public class ImportKbFileParseHarnessStep implements HarnessStep {

    private final ImportWorkflowRunner importWorkflowRunner;

    public ImportKbFileParseHarnessStep(ImportWorkflowRunner importWorkflowRunner) {
        this.importWorkflowRunner = importWorkflowRunner;
    }

    @Override
    public String stepCode() {
        return HarnessImportWorkflowSupport.IMPORT_KB_FILE_PARSE;
    }

    @Override
    public String stepName() {
        return "知识文件导入流水线";
    }

    @Override
    public String description() {
        return "执行 file→html→markdown→分块→向量化→落库 导入链路";
    }

    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        HarnessScenario scenario = ctx.scenario();
        if (!HarnessImportWorkflowSupport.isImportKbFileParse(scenario)) {
            return HarnessImportWorkflowSupport.skipStep(this, "非 IMPORT_KB_FILE_PARSE 场景，跳过");
        }
        Map<String, Object> params = scenario.inputParams() == null ? Map.of() : scenario.inputParams();
        Long fileId = readLong(params.get("fileId"));
        String fileUrl = readString(params.get("fileUrl"));
        if (fileId == null && StringUtils.hasText(fileUrl) && PlatformFileClient.isBaseFileUrl(fileUrl)) {
            fileId = PlatformFileClient.parseBaseFileId(fileUrl);
        }
        if (fileId == null) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, 0,
                    "请在 inputParams 中提供 fileId 或 base:// 文件引用",
                    Map.of("required", List.of("fileId", "fileUrl")));
        }

        long start = System.currentTimeMillis();
        try {
            ImportBizType bizType = ImportBizType.fromCode(readString(params.get("importBizType")));
            boolean aiEnhanced = readBoolean(params.get("aiEnhanced"), false);
            boolean autoQuiz = readBoolean(params.get("autoQuiz"), false);
            Long tenantId = readLong(params.get("tenantId"));
            Long userId = readLong(params.get("userId"));
            String title = readString(params.get("title"));
            String fileName = readString(params.get("fileName"));

            var state = ImportWorkflowRunner.buildState(
                    tenantId, userId, "harness-" + ctx.harnessId(), bizType,
                    fileId, fileUrl, fileName, title, aiEnhanced, autoQuiz);
            ImportWorkflowRunner.ImportWorkflowRunResult outcome = importWorkflowRunner.run(state);
            long durationMs = System.currentTimeMillis() - start;

            Map<String, Object> details = new LinkedHashMap<>();
            details.put("contentId", outcome.workflow().contentId());
            details.put("chunkCount", outcome.workflow().chunkCount());
            details.put("vectorized", outcome.workflow().vectorized());
            details.put("aiEnriched", outcome.workflow().aiEnriched());
            details.put("stages", outcome.stages());
            details.put("markdownPreview", outcome.markdownPreview());

            return new HarnessStepResult(
                    stepCode(), stepName(), true, durationMs,
                    "导入流水线完成，contentId=" + outcome.workflow().contentId(),
                    details);
        } catch (Exception ex) {
            return new HarnessStepResult(
                    stepCode(), stepName(), false, System.currentTimeMillis() - start,
                    "导入流水线失败: " + ex.getMessage(),
                    Map.of("exception", ex.getClass().getSimpleName()));
        }
    }

    private Long readLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String readString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private boolean readBoolean(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
