package cn.cyc.ai.cog.runtime.harness.support;

import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;

import java.util.Map;

/**
 * Harness 导入工作流场景识别与步骤跳过辅助。
 */
public final class HarnessImportWorkflowSupport {

    public static final String WORKFLOW_TYPE_KEY = "workflowType";
    public static final String IMPORT_KB_FILE_PARSE = "IMPORT_KB_FILE_PARSE";

    private HarnessImportWorkflowSupport() {
    }

    public static boolean isImportKbFileParse(HarnessScenario scenario) {
        if (scenario == null || scenario.inputParams() == null) {
            return false;
        }
        Object workflowType = scenario.inputParams().get(WORKFLOW_TYPE_KEY);
        return IMPORT_KB_FILE_PARSE.equals(String.valueOf(workflowType));
    }

    public static HarnessStepResult skipStep(HarnessStep step, String reason) {
        return new HarnessStepResult(
                step.stepCode(),
                step.stepName(),
                true,
                0,
                reason,
                Map.of("skipped", true, "reason", reason));
    }
}
