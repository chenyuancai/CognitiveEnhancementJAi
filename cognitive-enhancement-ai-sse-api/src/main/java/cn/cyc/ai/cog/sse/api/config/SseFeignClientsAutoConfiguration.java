package cn.cyc.ai.cog.sse.api.config;

import cn.cyc.ai.cog.sse.api.SseFeignClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 自动注册 {@link SseFeignClient}（引入 sse-api + openfeign 的消费端进程生效；sse-server 启动类已排除本配置）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@AutoConfiguration
@ConditionalOnClass(FeignClient.class)
@ConditionalOnProperty(prefix = "cog.sse", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableFeignClients(clients = SseFeignClient.class)
public class SseFeignClientsAutoConfiguration {
}
