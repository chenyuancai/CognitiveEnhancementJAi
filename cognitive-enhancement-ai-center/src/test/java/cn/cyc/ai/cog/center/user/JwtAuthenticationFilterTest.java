package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.common.context.AuthUser;
import cn.cyc.ai.cog.common.jwt.CompositeBearerIdentityParser;
import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import cn.cyc.ai.cog.runtime.security.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private ObjectProvider<JwtDecoder> adminJwtDecoderProvider;

    @Mock
    private ObjectProvider<JwtDecoder> appJwtDecoderProvider;

    private JwtProperties jwtProperties;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAuthEnabled(true);
        filter = new JwtAuthenticationFilter(jwtProperties, new ObjectMapper(),
                adminJwtDecoderProvider, appJwtDecoderProvider);
    }

    @Test
    void shouldAllowPermitAllPathWithoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectProtectedPathWithoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/center/models");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
    }

    @Test
    void shouldAcceptOAuth2BearerOnProtectedPath() throws Exception {
        when(adminJwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        filter = new JwtAuthenticationFilter(jwtProperties, new ObjectMapper(),
                adminJwtDecoderProvider, appJwtDecoderProvider);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/auth/me");
        request.addHeader(SecurityConstants.AUTHORIZATION_HEADER, "Bearer oauth2-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Jwt jwt = new Jwt("oauth2-token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of(SecurityConstants.CLAIM_USER_ID, 1,
                        SecurityConstants.CLAIM_USERNAME, "admin",
                        SecurityConstants.CLAIM_TENANT, "default",
                        SecurityConstants.CLAIM_ROLES, List.of("ADMIN")));
        when(jwtDecoder.decode("oauth2-token")).thenReturn(jwt);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(1L, request.getAttribute(AuthSupport.ATTR_USER_ID));
        assertEquals("default", request.getAttribute(AuthSupport.ATTR_TENANT_CODE));
    }

    @Test
    void shouldBypassWhenAuthDisabled() throws Exception {
        jwtProperties.setAuthEnabled(false);
        filter = new JwtAuthenticationFilter(jwtProperties, new ObjectMapper(),
                adminJwtDecoderProvider, appJwtDecoderProvider);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/center/models");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAllowTrustedGatewayHeadersWithoutBearer() throws Exception {
        jwtProperties.setTrustGatewayHeaders(true);
        filter = new JwtAuthenticationFilter(jwtProperties, new ObjectMapper(),
                adminJwtDecoderProvider, appJwtDecoderProvider);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/auth/me");
        request.addHeader(SecurityConstants.HEADER_USER_ID, "1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtDecoder, never()).decode(any());
    }
}
