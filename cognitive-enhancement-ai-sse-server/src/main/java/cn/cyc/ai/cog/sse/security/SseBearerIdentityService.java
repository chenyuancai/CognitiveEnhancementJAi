package cn.cyc.ai.cog.sse.security;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.CompositeBearerIdentityParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

/**
 * SSE 客户端 Bearer Token 身份解析。
 */
@Service
public class SseBearerIdentityService {

    private final CompositeBearerIdentityParser compositeBearerIdentityParser;

    public SseBearerIdentityService(
            @Value("${cog.jwt.secret:cognitive-enhancement-ai-default-secret-key}") String jwtSecret,
            @Qualifier("sseJwtDecoder") ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.compositeBearerIdentityParser = new CompositeBearerIdentityParser(jwtSecret, jwtDecoderProvider);
    }

    public AuthUser parseBearer(String authorizationHeader) {
        return compositeBearerIdentityParser.parseBearer(authorizationHeader);
    }
}
