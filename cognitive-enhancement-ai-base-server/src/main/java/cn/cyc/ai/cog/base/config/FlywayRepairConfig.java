package cn.cyc.ai.cog.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway 修复：migrate 前自动 repair，清理失败迁移记录（如历史 V2 种子脚本）。
 */
@Configuration
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
