package cn.cyc.ai.cog.base.dict.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 字典种类：0=枚举，1=字典。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Getter
@RequiredArgsConstructor
public enum DictKindEnum {

    ENUM(0, "枚举"),
    DICT(1, "字典");

    /** 值。 */
    private final int value;
    /** label。 */
    private final String label;
}
