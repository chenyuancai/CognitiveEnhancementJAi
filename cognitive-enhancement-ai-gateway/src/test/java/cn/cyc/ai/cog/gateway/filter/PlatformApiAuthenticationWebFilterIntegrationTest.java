package cn.cyc.ai.cog.gateway.filter;

import cn.cyc.ai.cog.common.jwt.SecurityConstants;
import cn.cyc.ai.cog.gateway.support.GatewayTestJwtSupport;
import cn.cyc.ai.cog.gateway.support.GatewayTestOAuth2JwtSupport;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * 网关 /api/** Bearer 强制验签与身份透传集成测试。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PlatformApiAuthenticationWebFilterIntegrationTest.TestJwtDecoderConfiguration.class)
class PlatformApiAuthenticationWebFilterIntegrationTest {

    private static WireMockServer wireMock;

    @Autowired
    private WebTestClient webTestClient;

    static {
        wireMock = new WireMockServer(wireMockConfig().dynamicPort());
        wireMock.start();
        wireMock.stubFor(get(urlEqualTo("/api/admin/system/health"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true}")));
        wireMock.stubFor(post(urlEqualTo("/api/app/billing/pay-callback"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true}")));
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMock != null) {
            wireMock.stop();
        }
    }

    @DynamicPropertySource
    static void registerGatewayRoute(DynamicPropertyRegistry registry) {
        registry.add("test.downstream.uri", wireMock::baseUrl);
    }

    @Test
    void shouldReturn401WhenProtectedApiWithoutBearer() {
        webTestClient.get()
                .uri("/api/admin/system/health")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo("C0401")
                .jsonPath("$.message").isEqualTo("未认证或令牌无效");
    }

    @Test
    void shouldAllowPermitAllPathWithoutBearer() {
        webTestClient.post()
                .uri("/api/app/billing/pay-callback")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRelayIdentityHeadersToDownstreamWithLegacyBearer() {
        webTestClient.get()
                .uri("/api/admin/system/health")
                .header(HttpHeaders.AUTHORIZATION, GatewayTestJwtSupport.legacyBearerToken())
                .exchange()
                .expectStatus().isOk();

        wireMock.verify(getRequestedFor(urlEqualTo("/api/admin/system/health"))
                .withHeader(SecurityConstants.HEADER_USER_ID, equalTo("1"))
                .withHeader(SecurityConstants.HEADER_USERNAME, equalTo("admin"))
                .withHeader(SecurityConstants.HEADER_TENANT, equalTo("default")));
    }

    @Test
    void shouldRelayIdentityHeadersToDownstreamWithOAuth2Bearer() {
        webTestClient.get()
                .uri("/api/admin/system/health")
                .header(HttpHeaders.AUTHORIZATION, GatewayTestOAuth2JwtSupport.oauth2BearerToken())
                .exchange()
                .expectStatus().isOk();

        wireMock.verify(getRequestedFor(urlEqualTo("/api/admin/system/health"))
                .withHeader(SecurityConstants.HEADER_USER_ID, equalTo("1"))
                .withHeader(SecurityConstants.HEADER_USERNAME, equalTo("admin"))
                .withHeader(SecurityConstants.HEADER_TENANT, equalTo("default")));
    }

    @TestConfiguration
    static class TestJwtDecoderConfiguration {

        @Bean
        @Primary
        JwtDecoder testJwtDecoder() {
            return NimbusJwtDecoder.withPublicKey(GatewayTestOAuth2JwtSupport.rsaPublicKey()).build();
        }
    }
}
