package cn.cyc.ai.cog.api.enums;

/**
 * 公告状态。
 */
public enum AnnouncementStatus implements CodedEnum {

    DRAFT,
    PUBLISHED;

    public static AnnouncementStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("公告状态码不能为空");
        }
        return AnnouncementStatus.valueOf(code);
    }

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
