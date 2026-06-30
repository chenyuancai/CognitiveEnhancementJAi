package cn.cyc.ai.cog.sse;

import cn.cyc.ai.cog.sse.api.config.SseFeignClientsAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SSE 推送服务启动入口（默认端口 8806，建议单副本部署）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@SpringBootApplication(
        scanBasePackages = "cn.cyc.ai.cog.sse",
        exclude = SseFeignClientsAutoConfiguration.class
)
public class SseServerApplication {

    /**
     * 应用入口。
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(SseServerApplication.class, args);
    }
}
