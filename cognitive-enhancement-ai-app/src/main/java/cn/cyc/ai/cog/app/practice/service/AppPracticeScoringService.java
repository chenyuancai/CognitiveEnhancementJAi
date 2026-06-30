package cn.cyc.ai.cog.app.practice.service;

import cn.cyc.ai.cog.app.contract.AppSseJsonWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 问答题 AI 评分 SSE 服务（七阶段进度 + Runtime SCORING）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Service
public class AppPracticeScoringService {

    /** SSE 阶段序列（末段 completed 携带 score） */
    private static final String[] STAGES = {
            "queued", "preparing", "requesting", "parsing", "persisting", "notifying", "completed"
    };

    private final AppPracticeSessionService sessionService;
    private final ObjectMapper objectMapper;

    public AppPracticeScoringService(AppPracticeSessionService sessionService, ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.objectMapper = objectMapper;
    }

    /**
     * 构建问答题评分 SSE 流。
     *
     * @param sessionCode 会话编码
     * @param answerId    作答记录 ID
     * @return SSE 响应体
     */
    public StreamingResponseBody streamEssayScore(String sessionCode, Long answerId) {
        return outputStream -> {
            try {
                for (int i = 0; i < STAGES.length - 1; i++) {
                    emit(outputStream, STAGES[i], sessionCode, null);
                    Thread.sleep(10L);
                }
                int score = sessionService.scoreEssayAnswer(answerId);
                emit(outputStream, "completed", sessionCode, score);
            } catch (Exception ex) {
                emitFailed(outputStream, sessionCode, ex.getMessage());
            }
        };
    }

    private void emit(OutputStream outputStream, String stage, String sessionId, Integer score) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", stage);
        payload.put("sessionId", sessionId);
        payload.put("stage", stage);
        if (score != null) {
            payload.put("score", score);
        }
        AppSseJsonWriter.write(outputStream, objectMapper, payload);
    }

    private void emitFailed(OutputStream outputStream, String sessionId, String error) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "failed");
        payload.put("sessionId", sessionId);
        payload.put("error", error == null ? "评分失败" : error);
        AppSseJsonWriter.write(outputStream, objectMapper, payload);
    }
}
