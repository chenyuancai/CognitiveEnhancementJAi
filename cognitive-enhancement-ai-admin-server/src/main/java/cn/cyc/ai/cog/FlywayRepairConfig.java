package cn.cyc.ai.cog;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway 修复配置，在 migrate 前自动执行 repair 清理失败的迁移记录。
 * <p>仅在持久化模式下启用，与 {@code cog.persistence.enabled} 保持一致。
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
