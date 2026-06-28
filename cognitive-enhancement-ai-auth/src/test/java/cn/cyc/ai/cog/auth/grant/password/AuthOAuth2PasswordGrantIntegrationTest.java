package cn.cyc.ai.cog.auth.grant.password;

import cn.cyc.ai.cog.auth.client.OAuth2PlatformClientFactory;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * OAuth2 password grant 发 Token 集成测试。
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthOAuth2PasswordGrantIntegrationTest {

    private static final String ADMIN_PASSWORD = "user1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void shouldIssueAccessTokenWithPlatformClaimsViaPasswordGrant() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", OAuth2PlatformClientFactory.CMS_CLIENT_ID);
        params.add("client_secret", "cms-secret");
        params.add("username", "admin");
        params.add("password", ADMIN_PASSWORD);
        params.add("scope", "cms.read");

        String response = mockMvc.perform(post("/oauth2/token")
                        .header(HttpHeaders.AUTHORIZATION, basicClientCredentials())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = com.jayway.jsonpath.JsonPath.read(response, "$.access_token");
        Jwt jwt = jwtDecoder.decode(accessToken);

        assertThat(jwt.getClaimAsString(SecurityConstants.CLAIM_USER_ID)).isEqualTo("1");
        assertThat(jwt.getClaimAsString(SecurityConstants.CLAIM_USERNAME)).isEqualTo("admin");
        assertThat(jwt.getClaimAsString(SecurityConstants.CLAIM_TENANT)).isEqualTo("default");
        assertThat(jwt.getClaimAsStringList(SecurityConstants.CLAIM_ROLES)).contains("ADMIN");
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", OAuth2PlatformClientFactory.CMS_CLIENT_ID);
        params.add("client_secret", "cms-secret");
        params.add("username", "admin");
        params.add("password", "wrong-password");

        mockMvc.perform(post("/oauth2/token")
                        .header(HttpHeaders.AUTHORIZATION, basicClientCredentials())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    private static String basicClientCredentials() {
        String raw = OAuth2PlatformClientFactory.CMS_CLIENT_ID + ":cms-secret";
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
