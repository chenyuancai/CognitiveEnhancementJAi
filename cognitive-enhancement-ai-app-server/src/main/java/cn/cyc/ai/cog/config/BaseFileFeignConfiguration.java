package cn.cyc.ai.cog.config;

import cn.cyc.ai.cog.base.api.file.EnableBaseFileFeign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 启用 base 文件 Feign 客户端（集成测试可通过 {@code cog.base.file.feign-enabled=false} 关闭）。
 */
@Configuration
@ConditionalOnProperty(name = "cog.base.file.feign-enabled", havingValue = "true", matchIfMissing = true)
@EnableBaseFileFeign
public class BaseFileFeignConfiguration {
}
