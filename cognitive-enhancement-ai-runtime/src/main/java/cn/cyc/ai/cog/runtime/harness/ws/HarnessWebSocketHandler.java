package cn.cyc.ai.cog.runtime.harness.ws;

import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessWsMessage;
import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessEngine;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessReportRepository;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Harness WebSocket 处理器，支持实时推送验证步骤进度。
 *
 * @author cyc
 */
public class HarnessWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(HarnessWebSocketHandler.class);

    private final HarnessEngine harnessEngine;
    private final List<HarnessStep> steps;
    private final HarnessReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    private final Map<String, Boolean> cancelledSessions = new ConcurrentHashMap<>();

    public HarnessWebSocketHandler(HarnessEngine harnessEngine,
                                    List<HarnessStep> steps,
                                    HarnessReportRepository reportRepository,
                                    ObjectMapper objectMapper) {
        this.harnessEngine = harnessEngine;
        this.steps = steps;
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String harnessId = "HAR-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 6);
        session.getAttributes().put("harnessId", harnessId);
        cancelledSessions.put(session.getId(), false);

        HarnessWsMessage connected = new HarnessWsMessage("CONNECTED",
                Map.of("harnessId", harnessId, "message", "WebSocket connected"));
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(connected)));
        log.info("Harness WebSocket connected, sessionId={}, harnessId={}", session.getId(), harnessId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        HarnessWsMessage wsMessage = objectMapper.readValue(payload, HarnessWsMessage.class);

        switch (wsMessage.type()) {
            case "RUN" -> handleRun(session, wsMessage);
            case "CANCEL" -> handleCancel(session);
            default -> log.warn("Unknown message type: {}", wsMessage.type());
        }
    }

    private void handleRun(WebSocketSession session, HarnessWsMessage wsMessage) throws Exception {
        String harnessId = (String) session.getAttributes().get("harnessId");
        Map<String, Object> payload = wsMessage.payload();

        @SuppressWarnings("unchecked")
        Map<String, Object> scenarioMap = (Map<String, Object>) payload.get("scenario");
        HarnessScenario scenario = objectMapper.convertValue(scenarioMap, HarnessScenario.class);

        HarnessContext context = new HarnessContext(
                harnessId, harnessId, Instant.now(),
                scenario, null, null, null, null, null, Map.of()
        );

        log.info("Harness WebSocket RUN started, harnessId={}", harnessId);

        CompletableFuture.runAsync(() -> {
            HarnessReport report = harnessEngine.run(steps, context, stepReport -> {
                try {
                    if (Boolean.TRUE.equals(cancelledSessions.get(session.getId()))) {
                        throw new RuntimeException("Harness cancelled by user");
                    }
                    HarnessWsMessage stepMsg = new HarnessWsMessage("STEP",
                            Map.of("step", stepReport));
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(stepMsg)));
                } catch (Exception e) {
                    log.error("Failed to send STEP message, harnessId={}", harnessId, e);
                }
            });
            reportRepository.save(report);

            try {
                HarnessWsMessage completeMsg = new HarnessWsMessage("COMPLETE",
                        Map.of("report", report));
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(completeMsg)));
                log.info("Harness WebSocket RUN completed, harnessId={}", harnessId);
            } catch (Exception e) {
                log.error("Failed to send COMPLETE message, harnessId={}", harnessId, e);
            }
        }).exceptionally(ex -> {
            try {
                HarnessWsMessage errorMsg = new HarnessWsMessage("ERROR",
                        Map.of("message", ex.getMessage()));
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMsg)));
            } catch (Exception e) {
                log.error("Failed to send ERROR message, harnessId={}", harnessId, e);
            }
            log.error("Harness WebSocket RUN failed, harnessId={}", harnessId, ex);
            return null;
        });
    }

    private void handleCancel(WebSocketSession session) {
        cancelledSessions.put(session.getId(), true);
        log.info("Harness WebSocket CANCEL received, sessionId={}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        cancelledSessions.remove(session.getId());
        log.info("Harness WebSocket closed, sessionId={}, status={}", session.getId(), status);
    }
}
