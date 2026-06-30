package cn.cyc.ai.cog.app.practice.web;

import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeChoiceAnswerRequest;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeCreateSessionRequest;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeEssayAnswerRequest;
import cn.cyc.ai.cog.app.practice.service.AppPracticeInsightService;
import cn.cyc.ai.cog.app.practice.service.AppPracticeScoringService;
import cn.cyc.ai.cog.app.practice.service.AppPracticeSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;

/**
 * C 端练习接口：会话创建、选择题/作文作答、AI 评分流与复盘。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Tag(name = "App-练习", description = "练习会话、作答与 AI 评分流")
@RestController
@RequestMapping("/api/practice")
public class AppPracticeController {

    private final AppPracticeSessionService sessionService;
    private final AppPracticeScoringService scoringService;
    private final AppPracticeInsightService insightService;

    public AppPracticeController(AppPracticeSessionService sessionService,
                                 AppPracticeScoringService scoringService,
                                 AppPracticeInsightService insightService) {
        this.sessionService = sessionService;
        this.scoringService = scoringService;
        this.insightService = insightService;
    }

    @Operation(summary = "创建练习会话")
    @PostMapping("/sessions")
    public ApiResponse<?> createSession(@RequestBody(required = false) AppPracticeCreateSessionRequest request) {
        AppPracticeCreateSessionRequest body = request == null ? new AppPracticeCreateSessionRequest() : request;
        return ApiResponse.success(sessionService.createSession(body));
    }

    @Operation(summary = "提交选择题")
    @PostMapping("/sessions/{id}/answers/choice")
    public ApiResponse<?> submitChoice(@PathVariable("id") String sessionId,
                                       @Valid @RequestBody AppPracticeChoiceAnswerRequest request) {
        return ApiResponse.success(sessionService.submitChoice(sessionId, request));
    }

    @Operation(summary = "提交问答题")
    @PostMapping("/sessions/{id}/answers/essay")
    public ApiResponse<?> submitEssay(@PathVariable("id") String sessionId,
                                      @Valid @RequestBody AppPracticeEssayAnswerRequest request) {
        return ApiResponse.success(sessionService.submitEssay(sessionId, request));
    }

    @Operation(summary = "问答题 AI 评分流")
    @GetMapping(value = "/sessions/{id}/answers/essay/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> essayStream(@PathVariable("id") String sessionId,
                                                               @RequestParam Long answerId) {
        StreamingResponseBody body = scoringService.streamEssayScore(sessionId, answerId);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "event-stream", StandardCharsets.UTF_8))
                .body(body);
    }

    @Operation(summary = "练习复盘")
    @GetMapping("/sessions/{id}/debrief")
    public ApiResponse<?> debrief(@PathVariable("id") String sessionId) {
        return ApiResponse.success(sessionService.debrief(sessionId));
    }

    @Operation(summary = "练习洞察")
    @GetMapping("/insight")
    public ApiResponse<?> insight() {
        return ApiResponse.success(insightService.insightForCurrentUser());
    }
}
