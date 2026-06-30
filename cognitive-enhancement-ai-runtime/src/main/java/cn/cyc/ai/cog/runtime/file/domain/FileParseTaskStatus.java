package cn.cyc.ai.cog.runtime.file.domain;

/**
 * 文件解析任务状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum FileParseTaskStatus {

    /** pending。 */
    PENDING,
    /** running。 */
    RUNNING,
    /** succeeded。 */
    SUCCEEDED,
    FAILED
}
