package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 生产模式：OAuth2 RS256 Bearer 直连 Starter 访问 /me（不经网关头）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"admin-it", "prod"})
@TestPropertySource(properties = {
        "cog.admin.dev-auth-bypass=false",
        "cog.admin.trust-gateway-headers=false",
        "cog.jwt.trust-gateway-headers=false",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/admin-it-schema.sql,classpath:db/center-it-schema.sql",
        "spring.sql.init.data-locations=classpath:db/admin-it-data.sql"
})
class AdminAuthMeWithOAuth2BearerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "adminJwtDecoder")
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void stubOAuth2Decoder() {
        Jwt jwt = new Jwt("oauth2-token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of(SecurityConstants.CLAIM_USER_ID, 1,
                        SecurityConstants.CLAIM_USERNAME, "admin",
                        SecurityConstants.CLAIM_TENANT, "default",
                        SecurityConstants.CLAIM_ROLES, List.of("ADMIN")));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
    }

    @Test
    void shouldReturn401WhenBearerMissing() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("A0401")));
    }

    @Test
    void shouldReturnAuthMeWithOAuth2Bearer() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me")
                        .header(SecurityConstants.AUTHORIZATION_HEADER, "Bearer oauth2-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.user.username", is("admin")))
                .andExpect(jsonPath("$.data.account.id", is("1")));
    }
}
