package cn.cyc.ai.cog.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 基础服务默认上下文配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@Component
@ConfigurationProperties(prefix = "cog.base")
public class BaseServiceProperties {

    /** 默认Biz编码。 */
    private String defaultBizCode = "cog";

    /** 默认ShareScope。 */
    private String defaultShareScope = "global";
}
