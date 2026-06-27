package cn.cyc.ai.cog.api.enums;

/**
 * 内容状态。
 */
public enum ContentStatus implements CodedEnum {

    DRAFT,
    PENDING,
    PUBLISHED,
    REJECTED,
    OFFLINE;

    public static ContentStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("内容状态码不能为空");
        }
        return ContentStatus.valueOf(code);
    }
}
