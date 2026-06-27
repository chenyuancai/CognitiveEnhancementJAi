package cn.cyc.ai.cog.common.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 当前登录用户上下文模型（借鉴 zcloud-core-secure 的 ZtxUser）。
 *
 * <p>由网关 / 资源服务从 OAuth2 JWT 中解析填充，贯穿一次请求。</p>
 *
 * @author cyc
 */
public class AuthUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String tenantCode;
    private Long tenantId;
    private List<String> roles;
    private List<String> authorities;

    public AuthUser() {
    }

    public AuthUser(Long userId, String username, String tenantCode,
                    List<String> roles, List<String> authorities) {
        this.userId = userId;
        this.username = username;
        this.tenantCode = tenantCode;
        this.roles = roles == null ? Collections.emptyList() : roles;
        this.authorities = authorities == null ? Collections.emptyList() : authorities;
    }

    /** 是否拥有指定角色。 */
    public boolean hasRole(String roleCode) {
        return roles != null && roles.contains(roleCode);
    }

    /** 是否拥有指定权限点。 */
    public boolean hasAuthority(String authority) {
        return authorities != null && authorities.contains(authority);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
