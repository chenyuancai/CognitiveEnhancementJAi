package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 生产方案 B：Gateway 验签后透传 X-User-* 头访问 /me。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"admin-it", "prod"})
@TestPropertySource(properties = {
        "cog.admin.dev-auth-bypass=false",
        "cog.admin.trust-gateway-headers=true",
        "cog.jwt.trust-gateway-headers=true",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/admin-it-schema.sql,classpath:db/center-it-schema.sql",
        "spring.sql.init.data-locations=classpath:db/admin-it-data.sql"
})
class AdminAuthMeWithTrustGatewayHeadersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn401WithoutGatewayHeaders() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("A0401")));
    }

    @Test
    void shouldReturnAuthMeWithGatewayHeaders() throws Exception {
        mockMvc.perform(get("/api/admin/auth/me")
                        .header(SecurityConstants.HEADER_USER_ID, "1")
                        .header(SecurityConstants.HEADER_USERNAME, "admin")
                        .header(SecurityConstants.HEADER_TENANT_ID, "1")
                        .header(SecurityConstants.HEADER_TENANT, "default")
                        .header(SecurityConstants.HEADER_ROLES, "ADMIN")
                        .header(SecurityConstants.HEADER_AUTHORITIES,
                                "admin:user:view,admin:order:refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.user.username", is("admin")))
                .andExpect(jsonPath("$.data.account.id", is("1")))
                .andExpect(jsonPath("$.data.permissions[?(@=='admin:order:refund')]").exists());
    }
}
