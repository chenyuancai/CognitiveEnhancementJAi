package cn.cyc.ai.cog.auth.config;

import cn.cyc.ai.cog.auth.client.OAuth2PlatformClientFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OAuth2 JDBC 存储与 cms-client 种子集成测试。
 */
@SpringBootTest
class AuthOAuth2JdbcStorageIntegrationTest {

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Test
    void shouldSeedCmsClientInJdbcRepository() {
        RegisteredClient client = registeredClientRepository.findByClientId(
                OAuth2PlatformClientFactory.CMS_CLIENT_ID);
        assertThat(client).isNotNull();
        assertThat(client.getClientId()).isEqualTo(OAuth2PlatformClientFactory.CMS_CLIENT_ID);
        assertThat(client.getAuthorizationGrantTypes())
                .extracting(Object::toString)
                .anyMatch(type -> type.contains("password"));
    }
}
