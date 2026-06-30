package cn.cyc.ai.cog.api.enums;

/**
 * 内容状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum ContentStatus implements CodedEnum {

    /** draft。 */
    DRAFT,
    /** pending。 */
    PENDING,
    /** published。 */
    PUBLISHED,
    /** rejected。 */
    REJECTED,
    OFFLINE;

    /**
     * 执行from编码。
     *
     * @param code 编码
     * @return 执行结果
     */
    public static ContentStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("内容状态码不能为空");
        }
        return ContentStatus.valueOf(code);
    }
}
