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

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final HarnessEngine harnessEngine;
    private final List<HarnessStep> harnessSteps;
    private final HarnessReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    public WebSocketConfig(HarnessEngine harnessEngine,
                           List<HarnessStep> harnessSteps,
                           HarnessReportRepository reportRepository,
                           ObjectMapper objectMapper) {
        this.harnessEngine = harnessEngine;
        this.harnessSteps = harnessSteps;
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(harnessWebSocketHandler(), "/ws/harness")
                .setAllowedOrigins("*");
    }

    @Bean
    public HarnessWebSocketHandler harnessWebSocketHandler() {
        return new HarnessWebSocketHandler(harnessEngine, harnessSteps, reportRepository, objectMapper);
    }
}
