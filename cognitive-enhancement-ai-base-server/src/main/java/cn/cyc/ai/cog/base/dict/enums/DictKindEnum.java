package cn.cyc.ai.cog.base.dict.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 字典种类：0=枚举，1=字典。
 */
@Getter
@RequiredArgsConstructor
public enum DictKindEnum {

    ENUM(0, "枚举"),
    DICT(1, "字典");

    private final int value;
    private final String label;
}
