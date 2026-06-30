package cn.cyc.ai.cog.auth.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 扩展的认证用户主体，携带用户 ID / 租户 / 角色 / 权限点，用于写入 JWT 声明。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class AuthUserDetails extends User {

    /** 用户 ID */
    private final Long userId;
    /** 租户 ID */
    private final Long tenantId;
    /** 租户编码。 */
    private final String tenantCode;
    /** roles。 */
    private final List<String> roles;
    /** permissions。 */
    private final List<String> permissions;

    /**
     * 创建AuthUserDetails。
     */
    public AuthUserDetails(String username, String password,
                           Collection<? extends GrantedAuthority> authorities,
                           Long userId, Long tenantId, String tenantCode,
                           List<String> roles, List<String> permissions) {
        super(username, password, authorities);
        this.userId = userId;
        this.tenantId = tenantId;
        this.tenantCode = tenantCode;
        this.roles = roles;
        this.permissions = permissions;
    }

    /**
     * 获取用户ID。
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 获取租户ID。
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * 获取租户编码。
     * @return 租户编码
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * 获取Roles。
     * @return Roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * 获取Permissions。
     * @return Permissions
     */
    public List<String> getPermissions() {
        return permissions;
    }
}
