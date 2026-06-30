package cn.cyc.ai.cog.api.enums;

/**
 * 内容导入任务状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum ContentImportJobStatus implements CodedEnum {

    /** pending。 */
    PENDING,
    /** running。 */
    RUNNING,
    /** 成功。 */
    SUCCESS,
    FAILED;

    /**
     * 执行from编码。
     *
     * @param code 编码
     * @return 执行结果
     */
    public static ContentImportJobStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("导入任务状态码不能为空");
        }
        return ContentImportJobStatus.valueOf(code);
    }
}
