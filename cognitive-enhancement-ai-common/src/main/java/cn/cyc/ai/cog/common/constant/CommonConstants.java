package cn.cyc.ai.cog.common.constant;

import cn.cyc.ai.cog.api.enums.EnableStatus;

/**
 * 平台通用常量（借鉴 zcloud-core-common 约定）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class CommonConstants {

    /**
     * 创建Common常量定义。
     */
    private CommonConstants() {
    }

    /** 默认租户编码（JWT/网关透传）。 */
    public static final String DEFAULT_TENANT = "platform";

    /** 默认平台租户 ID（2C 共享租户）。 */
    public static final long DEFAULT_TENANT_ID = 1L;

    /** 启用状态。 */
    public static final String STATUS_ENABLED = EnableStatus.ENABLED.code();

    /** 禁用状态。 */
    public static final String STATUS_DISABLED = EnableStatus.DISABLED.code();

    /** 逻辑未删除。 */
    public static final int NOT_DELETED = 0;

    /** 逻辑已删除。 */
    public static final int DELETED = 1;

    /** 超级管理员角色编码。 */
    public static final String ROLE_ADMIN = "ADMIN";
}
