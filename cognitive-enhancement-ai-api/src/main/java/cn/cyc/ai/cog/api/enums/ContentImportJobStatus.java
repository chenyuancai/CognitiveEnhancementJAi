package cn.cyc.ai.cog.api.enums;

/**
 * 内容导入任务状态。
 */
public enum ContentImportJobStatus implements CodedEnum {

    PENDING,
    RUNNING,
    SUCCESS,
    FAILED;

    public static ContentImportJobStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("导入任务状态码不能为空");
        }
        return ContentImportJobStatus.valueOf(code);
    }
}
