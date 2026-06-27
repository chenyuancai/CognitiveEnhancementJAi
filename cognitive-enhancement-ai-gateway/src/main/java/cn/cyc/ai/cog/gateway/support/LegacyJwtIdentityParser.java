package cn.cyc.ai.cog.gateway.support;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.LegacyHs256BearerParser;
import cn.cyc.ai.cog.gateway.config.GatewayJwtProperties;
import org.springframework.stereotype.Component;

/**
 * 解析登录接口签发的 HS256 JWT，委托 common 实现。
 */
@Component
public class LegacyJwtIdentityParser {

    private final LegacyHs256BearerParser delegate;

    public LegacyJwtIdentityParser(GatewayJwtProperties properties) {
        this.delegate = new LegacyHs256BearerParser(properties.getSecret());
    }

    public AuthUser parseBearer(String authorizationHeader) {
        return delegate.parseBearer(authorizationHeader);
    }
}
