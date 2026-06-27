package cn.cyc.ai.cog.admin.workbench.support;

import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.context.AuthUser;

/**
 * 工作台卡片权限校验（规范码 + 前端 alias 二选一）。
 */
public final class WorkbenchPermissionChecker {

    private WorkbenchPermissionChecker() {
    }

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
