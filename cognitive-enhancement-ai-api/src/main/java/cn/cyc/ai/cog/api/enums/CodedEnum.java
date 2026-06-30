package cn.cyc.ai.cog.api.enums;

/**
 * 与数据库/API 字符串对齐的枚举契约。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface CodedEnum {

    /** 持久化与 JSON 使用的状态码（默认取枚举名）。 */
    default String code() {
        return ((Enum<?>) this).name();
    }

    /** 判断给定字符串是否等于当前枚举码。 */
    default boolean matches(String value) {
        return value != null && code().equals(value);
    }
}
