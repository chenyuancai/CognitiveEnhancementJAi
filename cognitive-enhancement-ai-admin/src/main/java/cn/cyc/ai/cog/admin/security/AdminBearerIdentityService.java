package cn.cyc.ai.cog.admin.security;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.CompositeBearerIdentityParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

/**
 * Admin/App 本地 Bearer 解析：OAuth2 RS256 + legacy HS256。
 */
@Service
public class AdminBearerIdentityService {

    private final CompositeBearerIdentityParser compositeBearerIdentityParser;

    public AdminBearerIdentityService(@Value("${cog.jwt.secret:cognitive-enhancement-ai-default-secret-key}") String jwtSecret,
                                      @Qualifier("adminJwtDecoder") ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.compositeBearerIdentityParser = new CompositeBearerIdentityParser(jwtSecret, jwtDecoderProvider);
    }

    public AuthUser parseBearer(String authorizationHeader) {
        return compositeBearerIdentityParser.parseBearer(authorizationHeader);
    }
}
