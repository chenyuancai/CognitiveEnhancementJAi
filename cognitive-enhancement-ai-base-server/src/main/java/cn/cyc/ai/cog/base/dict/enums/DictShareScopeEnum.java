package cn.cyc.ai.cog.base.dict.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 字典共享范围。
 */
@Getter
@RequiredArgsConstructor
public enum DictShareScopeEnum {

    GLOBAL("global", "全局共享"),
    BIZ("biz", "业务域"),
    TENANT("tenant", "租户");

    private final String value;
    private final String label;
}
