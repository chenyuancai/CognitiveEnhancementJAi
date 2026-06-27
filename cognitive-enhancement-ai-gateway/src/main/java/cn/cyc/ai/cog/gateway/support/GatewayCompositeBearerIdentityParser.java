package cn.cyc.ai.cog.gateway.support;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.CompositeBearerIdentityParser;
import cn.cyc.ai.cog.gateway.config.GatewayJwtProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

/**
 * 网关 Bearer 解析 Bean，委托 common {@link CompositeBearerIdentityParser}。
 */
@Component
public class GatewayCompositeBearerIdentityParser {

    private final CompositeBearerIdentityParser delegate;

    public GatewayCompositeBearerIdentityParser(GatewayJwtProperties jwtProperties,
                                                ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.delegate = new CompositeBearerIdentityParser(jwtProperties.getSecret(), jwtDecoderProvider);
    }

    public AuthUser parseBearer(String authorizationHeader) {
        return delegate.parseBearer(authorizationHeader);
    }
}
