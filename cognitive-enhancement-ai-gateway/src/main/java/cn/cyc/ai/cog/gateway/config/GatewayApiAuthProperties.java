package cn.cyc.ai.cog.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 网关 /api/** 鉴权：除白名单外强制有效 Bearer。
 */
@Data
@ConfigurationProperties(prefix = "cog.gateway.api-auth")
public class GatewayApiAuthProperties {

    /** 是否启用 /api/** Bearer 强制验签（方案 B）；方案 A 纯转发时应为 false。 */
    private boolean enabled = false;

    /** 免 Bearer 的 Ant 路径（与 starter {@code cog.jwt.permit-all} 对齐）。 */
    private List<String> permitAll = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/users/login",
            "/api/users/register",
            "/api/app/billing/pay-callback/**"
    );
}
