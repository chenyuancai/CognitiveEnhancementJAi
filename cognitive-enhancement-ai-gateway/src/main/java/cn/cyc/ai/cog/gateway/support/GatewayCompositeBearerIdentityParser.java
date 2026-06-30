package cn.cyc.ai.cog.gateway.support;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.CompositeBearerIdentityParser;
import cn.cyc.ai.cog.gateway.config.GatewayJwtProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

/**
 * 网关 Bearer 解析 Bean，委托 common {@link CompositeBearerIdentityParser}。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class GatewayCompositeBearerIdentityParser {

    /** delegate。 */
    private final CompositeBearerIdentityParser delegate;

    /**
     * 创建GatewayCompositeBearerIdentityParser。
     */
    public GatewayCompositeBearerIdentityParser(GatewayJwtProperties jwtProperties,
                                                ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.delegate = new CompositeBearerIdentityParser(jwtProperties.getSecret(), jwtDecoderProvider);
    }

    /**
     * 执行parseBearer。
     *
     * @param authorizationHeader authorizationHeader
     * @return 执行结果
     */
    public AuthUser parseBearer(String authorizationHeader) {
        return delegate.parseBearer(authorizationHeader);
    }
}
