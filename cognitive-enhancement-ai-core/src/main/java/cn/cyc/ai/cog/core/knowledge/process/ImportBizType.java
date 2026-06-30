package cn.cyc.ai.cog.core.knowledge.process;

/**
 * 导入业务类型：前端创建任务时必传，决定流水线阶段开关。
 */
public enum ImportBizType {

    KNOWLEDGE_DOCUMENT,
    KNOWLEDGE_URL,
    COURSE_HANDOUT,
    EXAM_PAPER,
    MISTAKE_ARCHIVE,
    PRACTICE_SOURCE;

    public static ImportBizType fromCode(String code) {
        if (code == null || code.isBlank()) {
            return KNOWLEDGE_DOCUMENT;
        }
        return valueOf(code.trim().toUpperCase());
    }
}
