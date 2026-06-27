package cn.cyc.ai.cog.auth.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 扩展的认证用户主体，携带用户 ID / 租户 / 角色 / 权限点，用于写入 JWT 声明。
 *
 * @author cyc
 */
public class AuthUserDetails extends User {

    private final Long userId;
    private final Long tenantId;
    private final String tenantCode;
    private final List<String> roles;
    private final List<String> permissions;

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

    public Long getUserId() {
        return userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
