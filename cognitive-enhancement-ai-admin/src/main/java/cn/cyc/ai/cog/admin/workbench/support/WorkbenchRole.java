package cn.cyc.ai.cog.admin.workbench.support;

import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.context.AuthUser;

import java.util.List;

/**
 * 工作台首页角色（2A 角色化卡片矩阵）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public enum WorkbenchRole {

    /** 管理后台。 */
    ADMIN,
    /** operator。 */
    OPERATOR,
    /** 内容。 */
    CONTENT,
    SUPPORT;

    /**
     * 按优先级解析当前用户主角色：ADMIN &gt; OPERATOR &gt; CONTENT &gt; SUPPORT。
     */
    public static WorkbenchRole resolve(AuthUser user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return ADMIN;
        }
        List<String> roles = user.getRoles();
        if (roles.contains(CommonConstants.ROLE_ADMIN)) {
            return ADMIN;
        }
        if (roles.contains("OPERATOR")) {
            return OPERATOR;
        }
        if (roles.contains("CONTENT")) {
            return CONTENT;
        }
        if (roles.contains("SUPPORT")) {
            return SUPPORT;
        }
        return ADMIN;
    }
}
