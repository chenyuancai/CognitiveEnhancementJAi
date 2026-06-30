package cn.cyc.ai.cog.common.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 当前登录用户上下文模型（借鉴 zcloud-core-secure 的 ZtxUser）。
 * <p>由网关 / 资源服务从 OAuth2 JWT 中解析填充，贯穿一次请求。</p>
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class AuthUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    private Long userId;
    /** username。 */
    private String username;
    /** 租户编码。 */
    private String tenantCode;
    /** 租户 ID */
    private Long tenantId;
    /** roles。 */
    private List<String> roles;
    /** authorities。 */
    private List<String> authorities;

    /**
     * 创建AuthUser。
     */
    public AuthUser() {
    }

    /**
     * 创建AuthUser。
     */
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

    /**
     * 获取用户ID。
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID。
     *
     * @param userId 用户 ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取Username。
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置Username。
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取租户编码。
     * @return 租户编码
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * 设置租户编码。
     *
     * @param tenantCode 租户编码
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    /**
     * 获取租户ID。
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID。
     *
     * @param tenantId 租户 ID
     */
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 获取Roles。
     * @return Roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * 设置Roles。
     *
     * @param roles roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * 获取Authorities。
     * @return Authorities
     */
    public List<String> getAuthorities() {
        return authorities;
    }

    /**
     * 设置Authorities。
     *
     * @param authorities authorities
     */
    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
