package cn.cyc.ai.cog.runtime.config;

import cn.cyc.ai.cog.runtime.harness.spi.HarnessEngine;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessReportRepository;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.ws.HarnessWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * WebSocket配置
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /** harnessEngine。 */
    private final HarnessEngine harnessEngine;
    /** harnessSteps。 */
    private final List<HarnessStep> harnessSteps;
    /** report仓储。 */
    private final HarnessReportRepository reportRepository;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    /**
     * 创建WebSocket配置。
     */
    public WebSocketConfig(HarnessEngine harnessEngine,
                           List<HarnessStep> harnessSteps,
                           HarnessReportRepository reportRepository,
                           ObjectMapper objectMapper) {
        this.harnessEngine = harnessEngine;
        this.harnessSteps = harnessSteps;
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行registerWebSocketHandlers。
     *
     * @param registry registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(harnessWebSocketHandler(), "/ws/harness")
                .setAllowedOrigins("*");
    }

    /**
     * 执行harnessWebSocket处理器。
     * @return 执行结果
     */
    @Bean
    public HarnessWebSocketHandler harnessWebSocketHandler() {
        return new HarnessWebSocketHandler(harnessEngine, harnessSteps, reportRepository, objectMapper);
    }
}
