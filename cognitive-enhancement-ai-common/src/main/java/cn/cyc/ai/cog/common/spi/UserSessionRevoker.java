package cn.cyc.ai.cog.common.spi;

/**
 * 用户会话吊销 SPI（封禁等场景撤销已签发 OAuth2 令牌）。
 */
public interface UserSessionRevoker {

    /**
     * 按登录主体名吊销全部授权记录。
     *
     * @param principalName 登录名（与 OAuth2 principal_name 一致）
     */
    void revokeByPrincipalName(String principalName);
}
