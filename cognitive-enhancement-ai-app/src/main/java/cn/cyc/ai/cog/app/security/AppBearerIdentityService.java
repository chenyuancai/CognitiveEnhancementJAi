package cn.cyc.ai.cog.app.security;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.CompositeBearerIdentityParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

/**
 * C 端 Bearer Token 身份解析服务。
 * <p>
 * 支持 OAuth2 RS256（JWK）与 legacy HS256 两种 JWT 格式。
 * </p>
 */
@Service
public class AppBearerIdentityService {

    /** 组合 Bearer 解析器 */
    private final CompositeBearerIdentityParser compositeBearerIdentityParser;

    /**
     * @param jwtSecret          HS256 密钥
     * @param jwtDecoderProvider OAuth2 JWT 解码器（可选）
     */
    public AppBearerIdentityService(@Value("${cog.jwt.secret:cognitive-enhancement-ai-default-secret-key}") String jwtSecret,
                                    @Qualifier("appJwtDecoder") ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.compositeBearerIdentityParser = new CompositeBearerIdentityParser(jwtSecret, jwtDecoderProvider);
    }

    /**
     * 从 Authorization 头解析当前用户。
     *
     * @param authorizationHeader Bearer Token 头
     * @return 解析成功返回用户上下文，否则 null
     */
    public AuthUser parseBearer(String authorizationHeader) {
        return compositeBearerIdentityParser.parseBearer(authorizationHeader);
    }
}
