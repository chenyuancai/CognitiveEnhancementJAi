package cn.cyc.ai.cog.base.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway 修复：migrate 前自动 repair，清理失败迁移记录（如历史 V2 种子脚本）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayRepairConfig {

    /**
     * 执行flywayMigration策略。
     * @return 执行结果
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
