package cn.cyc.ai.cog.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth2 存储模式：jdbc（生产）或 memory（本地快速启动）。
 */
@Data
@ConfigurationProperties(prefix = "cog.auth.storage")
public class AuthStorageProperties {

    /** 存储后端：jdbc | memory。 */
    private String mode = "jdbc";
}
