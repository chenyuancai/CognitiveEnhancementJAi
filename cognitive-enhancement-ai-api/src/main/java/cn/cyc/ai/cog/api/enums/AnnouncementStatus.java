package cn.cyc.ai.cog.api.enums;

/**
 * 公告状态。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum AnnouncementStatus implements CodedEnum {

    /** draft。 */
    DRAFT,
    PUBLISHED;

    /**
     * 执行from编码。
     *
     * @param code 编码
     * @return 执行结果
     */
    public static AnnouncementStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("公告状态码不能为空");
        }
        return AnnouncementStatus.valueOf(code);
    }

    /**
     * 判断是否为Valid。
     *
     * @param code 编码
     * @return 是否满足条件
     */
    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        for (AnnouncementStatus status : values()) {
            if (status.matches(code)) {
                return true;
            }
        }
        return false;
    }
}
