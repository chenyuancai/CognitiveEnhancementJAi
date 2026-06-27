package cn.cyc.ai.cog.auth.config;

import cn.cyc.ai.cog.auth.client.OAuth2PlatformClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * OAuth2 客户端与授权信息存储：JDBC（默认）或内存（开发/测试）。
 */
@Configuration
@EnableConfigurationProperties(AuthStorageProperties.class)
public class AuthOAuth2StorageConfiguration {

    @Bean
    @ConditionalOnProperty(name = "cog.auth.storage.mode", havingValue = "jdbc", matchIfMissing = true)
    public RegisteredClientRepository jdbcRegisteredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "cog.auth.storage.mode", havingValue = "jdbc", matchIfMissing = true)
    public OAuth2AuthorizationService jdbcOAuth2AuthorizationService(JdbcTemplate jdbcTemplate,
                                                                      RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "cog.auth.storage.mode", havingValue = "memory")
    public RegisteredClientRepository inMemoryRegisteredClientRepository() {
        return new InMemoryRegisteredClientRepository(OAuth2PlatformClientFactory.buildCmsClient());
    }

    @Bean
    @ConditionalOnProperty(name = "cog.auth.storage.mode", havingValue = "memory")
    public OAuth2AuthorizationService inMemoryOAuth2AuthorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }
}
