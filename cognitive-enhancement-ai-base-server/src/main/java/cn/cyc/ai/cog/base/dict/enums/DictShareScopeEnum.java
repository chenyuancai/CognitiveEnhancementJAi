package cn.cyc.ai.cog.base.dict.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 字典共享范围。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Getter
@RequiredArgsConstructor
public enum DictShareScopeEnum {

    GLOBAL("global", "全局共享"),
    BIZ("biz", "业务域"),
    TENANT("tenant", "租户");

    /** 值。 */
    private final String value;
    /** label。 */
    private final String label;
}
