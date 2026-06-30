package cn.cyc.ai.cog.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 网关请求日志配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.gateway.log")
public class GatewayLogProperties {

    /** 是否打印请求入参日志。 */
    private boolean enabled = true;

    /** 是否打印 Query 参数。 */
    private boolean logQuery = true;

    /** 是否打印请求头（敏感头脱敏）。 */
    private boolean logHeaders = true;

    /** 是否打印请求体（仅文本类 Content-Type）。 */
    private boolean logBody = true;

    /** 请求体日志最大长度，超出截断。 */
    private int maxBodyLength = 2048;

    /** 不打印日志的路径（Ant 风格）。 */
    private List<String> excludePaths = List.of("/actuator/**");
}
