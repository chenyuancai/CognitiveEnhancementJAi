package cn.cyc.ai.cog.base.file.enums;

/**
 * 文件生命周期状态（持久化为 TINYINT）。
 */
public enum FileStatusEnum {

    UNCONFIRMED(1),
    CONFIRMED(2);

    private final int code;

    FileStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FileStatusEnum fromCode(int code) {
        for (FileStatusEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知文件状态: " + code);
    }
}
