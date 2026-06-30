package cn.cyc.ai.cog.runtime.file.domain;

/**
 * 文件上传状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum FileUploadStatus {

    /** uploaded。 */
    UPLOADED,
    /** parsing。 */
    PARSING,
    /** parsed。 */
    PARSED,
    FAILED
}
