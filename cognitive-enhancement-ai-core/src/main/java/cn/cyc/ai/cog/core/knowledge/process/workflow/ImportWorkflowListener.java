package cn.cyc.ai.cog.core.knowledge.process.workflow;

import cn.cyc.ai.cog.core.knowledge.process.ImportWorkflowStage;

/**
 * 导入工作流进度回调。
 */
@FunctionalInterface
public interface ImportWorkflowListener {

    void onStage(ImportWorkflowStage stage, String message);
}
