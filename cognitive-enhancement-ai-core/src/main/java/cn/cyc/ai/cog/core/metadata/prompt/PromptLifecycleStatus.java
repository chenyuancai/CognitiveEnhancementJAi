package cn.cyc.ai.cog.core.metadata.prompt;

/**
 * Prompt 模板生命周期状态。
 *
 * @author cyc
 */
public enum PromptLifecycleStatus {

    /**
     * 草稿，运行时不可见。
     */
    DRAFT,

    /**
     * 已发布，可被运行时解析。
     */
    PUBLISHED,

    /**
     * 已下线，默认运行时不可见；灰度基线版本仍可被 ReleaseRouter 引用。
     */
    OFFLINE
}
