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
 */
@SpringBootApplication
@EnableConfigurationProperties({
        GatewayJwtProperties.class,
        GatewayApiAuthProperties.class,
        GatewayLogProperties.class
})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
