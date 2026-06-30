package cn.cyc.ai.cog.auth.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

/**
 * JDBC 模式下幂等写入 cms-client 注册信息。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
@ConditionalOnProperty(name = "cog.auth.storage.mode", havingValue = "jdbc", matchIfMissing = true)
public class OAuth2PlatformClientRegistrar implements ApplicationRunner {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(OAuth2PlatformClientRegistrar.class);

    /** registered客户端仓储。 */
    private final RegisteredClientRepository registeredClientRepository;

    /**
     * 创建OAuth2PlatformClientRegistrar。
     *
     * @param registeredClientRepository registered客户端仓储
     */
    public OAuth2PlatformClientRegistrar(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    /**
     * 执行操作。
     *
     * @param args args
     * @return 执行结果
     */
    @Override
    public void run(ApplicationArguments args) {
        if (registeredClientRepository.findByClientId(OAuth2PlatformClientFactory.CMS_CLIENT_ID) != null) {
            return;
        }
        registeredClientRepository.save(OAuth2PlatformClientFactory.buildCmsClient());
        log.info("Seeded OAuth2 registered client: {}", OAuth2PlatformClientFactory.CMS_CLIENT_ID);
    }
}
