package cn.cyc.ai.cog.admin.workbench.support;

import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.context.AuthUser;

/**
 * 工作台卡片权限校验（规范码 + 前端 alias 二选一）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class WorkbenchPermissionChecker {

    /**
     * 创建WorkbenchPermissionChecker。
     */
    private WorkbenchPermissionChecker() {
    }

    /**
     * 判断是否包含Any。
     *
     * @param user 用户
     * @param permissionCodes 权限Codes
     * @return 是否包含
     */
    public static boolean hasAny(AuthUser user, String... permissionCodes) {
        if (user == null) {
            return false;
        }
        if (user.hasRole(CommonConstants.ROLE_ADMIN)) {
            return true;
        }
        if (permissionCodes == null) {
            return true;
        }
        for (String code : permissionCodes) {
            if (code != null && user.hasAuthority(code)) {
                return true;
            }
        }
        return permissionCodes.length == 0;
    }
}
