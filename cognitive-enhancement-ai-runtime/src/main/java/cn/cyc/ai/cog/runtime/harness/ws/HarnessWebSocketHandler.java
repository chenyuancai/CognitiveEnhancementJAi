package cn.cyc.ai.cog.runtime.harness.ws;

import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessWsMessage;
import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessCancellation;
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
 * Harness WebSocket 处理器，支持实时推送验证步骤进度与可中断取消。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class HarnessWebSocketHandler extends TextWebSocketHandler {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(HarnessWebSocketHandler.class);

    /** ATTRHARNESSID */
    private static final String ATTR_HARNESS_ID = "harnessId";
    /** ATTRCANCELLATION。 */
    private static final String ATTR_CANCELLATION = "cancellation";
    /** ATTRRUNFUTURE。 */
    private static final String ATTR_RUN_FUTURE = "runFuture";

    /** harnessEngine。 */
    private final HarnessEngine harnessEngine;
    /** steps。 */
    private final List<HarnessStep> steps;
    /** report仓储。 */
    private final HarnessReportRepository reportRepository;
    /** JSON 序列化器 */
    private final ObjectMapper objectMapper;

    private final Map<String, HarnessCancellation> cancellationBySession = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<Void>> runFutureBySession = new ConcurrentHashMap<>();

    /**
     * 创建HarnessWebSocket处理器。
     */
    public HarnessWebSocketHandler(HarnessEngine harnessEngine,
                                    List<HarnessStep> steps,
                                    HarnessReportRepository reportRepository,
                                    ObjectMapper objectMapper) {
        this.harnessEngine = harnessEngine;
        this.steps = steps;
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行afterConnectionEstablished。
     *
     * @param session 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String harnessId = "HAR-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 6);
        session.getAttributes().put(ATTR_HARNESS_ID, harnessId);

        HarnessWsMessage connected = new HarnessWsMessage("CONNECTED",
                Map.of("harnessId", harnessId, "message", "WebSocket connected"));
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(connected)));
        log.info("Harness WebSocket connected, sessionId={}, harnessId={}", session.getId(), harnessId);
    }

    /**
     * 处理请求。
     *
     * @param session 会话
     * @param message 消息
     */
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

    /**
     * 处理请求。
     *
     * @param session 会话
     * @param wsMessage ws消息
     */
    private void handleRun(WebSocketSession session, HarnessWsMessage wsMessage) throws Exception {
        CompletableFuture<Void> existingFuture = runFutureBySession.get(session.getId());
        if (existingFuture != null && !existingFuture.isDone()) {
            sendMessage(session, new HarnessWsMessage("ERROR",
                    Map.of("message", "Harness 正在执行中，请稍后再试")));
            return;
        }

        String harnessId = (String) session.getAttributes().get(ATTR_HARNESS_ID);
        Map<String, Object> payload = wsMessage.payload();

        @SuppressWarnings("unchecked")
        Map<String, Object> scenarioMap = (Map<String, Object>) payload.get("scenario");
        HarnessScenario scenario = objectMapper.convertValue(scenarioMap, HarnessScenario.class);

        HarnessContext context = new HarnessContext(
                harnessId, harnessId, Instant.now(),
                scenario, null, null, null, null, null, Map.of()
        );

        HarnessCancellation cancellation = HarnessCancellation.create();
        cancellationBySession.put(session.getId(), cancellation);
        session.getAttributes().put(ATTR_CANCELLATION, cancellation);

        log.info("Harness WebSocket RUN started, harnessId={}", harnessId);

        CompletableFuture<Void> runFuture = CompletableFuture.runAsync(() -> {
            HarnessReport report = harnessEngine.run(steps, context, stepReport -> {
                try {
                    sendMessage(session, new HarnessWsMessage("STEP", Map.of("step", stepReport)));
                } catch (Exception e) {
                    log.error("Failed to send STEP message, harnessId={}", harnessId, e);
                }
            }, cancellation);
            reportRepository.save(report);

            try {
                sendMessage(session, new HarnessWsMessage("COMPLETE", Map.of("report", report)));
                log.info("Harness WebSocket RUN completed, harnessId={}, status={}", harnessId, report.status());
            } catch (Exception e) {
                log.error("Failed to send COMPLETE message, harnessId={}", harnessId, e);
            }
        }).whenComplete((ignored, ex) -> {
            runFutureBySession.remove(session.getId());
            cancellationBySession.remove(session.getId());
            session.getAttributes().remove(ATTR_CANCELLATION);
            session.getAttributes().remove(ATTR_RUN_FUTURE);
            if (ex != null && !(ex instanceof java.util.concurrent.CancellationException)) {
                log.error("Harness WebSocket RUN failed, harnessId={}", harnessId, ex);
                try {
                    sendMessage(session, new HarnessWsMessage("ERROR",
                            Map.of("message", ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage())));
                } catch (Exception sendEx) {
                    log.error("Failed to send ERROR message, harnessId={}", harnessId, sendEx);
                }
            }
        });

        runFutureBySession.put(session.getId(), runFuture);
        session.getAttributes().put(ATTR_RUN_FUTURE, runFuture);
    }

    /**
     * 处理请求。
     *
     * @param session 会话
     */
    private void handleCancel(WebSocketSession session) throws Exception {
        HarnessCancellation cancellation = cancellationBySession.get(session.getId());
        if (cancellation == null) {
            cancellation = (HarnessCancellation) session.getAttributes().get(ATTR_CANCELLATION);
        }
        if (cancellation != null) {
            cancellation.cancel();
        }

        sendMessage(session, new HarnessWsMessage("CANCEL",
                Map.of("message", "已接收取消请求，正在中断后续步骤")));
        log.info("Harness WebSocket CANCEL received, sessionId={}", session.getId());
    }

    /**
     * 执行afterConnectionClosed。
     *
     * @param session 会话
     * @param status 状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        HarnessCancellation cancellation = cancellationBySession.remove(session.getId());
        if (cancellation != null) {
            cancellation.cancel();
        }
        runFutureBySession.remove(session.getId());
        log.info("Harness WebSocket closed, sessionId={}, status={}", session.getId(), status);
    }

    /**
     * 执行send消息。
     *
     * @param session 会话
     * @param message 消息
     */
    private void sendMessage(WebSocketSession session, HarnessWsMessage message) throws Exception {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }
    }
}
