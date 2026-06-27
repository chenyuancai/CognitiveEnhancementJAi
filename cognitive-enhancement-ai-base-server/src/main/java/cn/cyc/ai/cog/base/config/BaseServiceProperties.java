package cn.cyc.ai.cog.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 基础服务默认上下文配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "cog.base")
public class BaseServiceProperties {

    private String defaultBizCode = "cog";

    private String defaultShareScope = "global";
}
