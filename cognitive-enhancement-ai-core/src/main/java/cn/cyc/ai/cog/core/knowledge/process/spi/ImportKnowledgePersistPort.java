package cn.cyc.ai.cog.core.knowledge.process.spi;

import cn.cyc.ai.cog.core.knowledge.process.workflow.ImportWorkflowState;

/**
 * 导入结果落库（内容 + 分块 + 向量索引）。
 */
public interface ImportKnowledgePersistPort {

    /**
     * 持久化解析结果，返回内容 ID。
     */
    Long persist(ImportWorkflowState state);
}
