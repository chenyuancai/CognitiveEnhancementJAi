package cn.cyc.ai.cog.platform.iam.support;

import cn.cyc.ai.cog.common.spi.UserSessionRevoker;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 基于 JDBC 删除 oauth2_authorization 记录的会话吊销实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class JdbcUserSessionRevoker implements UserSessionRevoker {

    /** JDBC 模板 */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate JDBC 模板
     */
    public JdbcUserSessionRevoker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 执行revoke人Principal名称。
     *
     * @param principalName principal名称
     */
    @Override
    public void revokeByPrincipalName(String principalName) {
        if (!StringUtils.hasText(principalName)) {
            return;
        }
        try {
            jdbcTemplate.update("DELETE FROM oauth2_authorization WHERE principal_name = ?", principalName.trim());
        } catch (Exception ignored) {
            // 测试或未启用 OAuth2 JDBC 时表可能不存在，忽略
        }
    }
}
