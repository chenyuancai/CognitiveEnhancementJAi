package cn.cyc.ai.cog.gateway;

import cn.cyc.ai.cog.gateway.config.GatewayApiAuthProperties;
import cn.cyc.ai.cog.gateway.config.GatewayJwtProperties;
import cn.cyc.ai.cog.gateway.config.GatewayLogProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * API 网关启动类（Spring Cloud Gateway，独立进程）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@SpringBootApplication
@EnableConfigurationProperties({
        GatewayJwtProperties.class,
        GatewayApiAuthProperties.class,
        GatewayLogProperties.class
})
public class GatewayApplication {

    /**
     * 应用入口。
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
