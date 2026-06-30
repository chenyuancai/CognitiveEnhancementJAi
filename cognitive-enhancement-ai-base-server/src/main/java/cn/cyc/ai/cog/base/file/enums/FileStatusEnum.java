package cn.cyc.ai.cog.base.file.enums;

/**
 * 文件生命周期状态（持久化为 TINYINT）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum FileStatusEnum {

    UNCONFIRMED(1),
    CONFIRMED(2);

    /** 编码。 */
    private final int code;

    /**
     * 创建FileStatusEnum 枚举。
     *
     * @param code 编码
     */
    FileStatusEnum(int code) {
        this.code = code;
    }

    /**
     * 获取编码。
     * @return 编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 执行from编码。
     *
     * @param code 编码
     * @return 执行结果
     */
    public static FileStatusEnum fromCode(int code) {
        for (FileStatusEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知文件状态: " + code);
    }
}
