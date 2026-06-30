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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AdminBearerIdentityService {

    /** compositeBearerIdentityParser。 */
    private final CompositeBearerIdentityParser compositeBearerIdentityParser;

    /**
     * 创建管理后台BearerIdentity服务。
     */
    public AdminBearerIdentityService(@Value("${cog.jwt.secret:cognitive-enhancement-ai-default-secret-key}") String jwtSecret,
                                      @Qualifier("adminJwtDecoder") ObjectProvider<JwtDecoder> jwtDecoderProvider) {
        this.compositeBearerIdentityParser = new CompositeBearerIdentityParser(jwtSecret, jwtDecoderProvider);
    }

    /**
     * 执行parseBearer。
     *
     * @param authorizationHeader authorizationHeader
     * @return 执行结果
     */
    public AuthUser parseBearer(String authorizationHeader) {
        return compositeBearerIdentityParser.parseBearer(authorizationHeader);
    }
}
