package cn.cyc.ai.cog.app.practice.service;

import cn.cyc.ai.cog.app.dto.AppLearningInvokeRequest;
import cn.cyc.ai.cog.app.practice.assembler.AppPracticeVoAssembler;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeChoiceAnswerRequest;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeChoiceResultVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeCreateSessionRequest;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeDebriefVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeEssayAnswerRequest;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeEssaySubmitVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeQuestionVO;
import cn.cyc.ai.cog.app.practice.dto.AppPracticeSessionVO;
import cn.cyc.ai.cog.app.practice.support.PracticeQuestionFactory;
import cn.cyc.ai.cog.app.service.AppLearningService;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringMistakeService;
import cn.cyc.ai.cog.common.context.TenantContext;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.platform.practice.entity.PracticeAnswerEntity;
import cn.cyc.ai.cog.platform.practice.entity.PracticeSessionEntity;
import cn.cyc.ai.cog.platform.practice.entity.ReviewPendingEntity;
import cn.cyc.ai.cog.platform.practice.spi.PracticePersistencePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 练习会话编排：创建会话、作答、复盘与低分错题联动。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Service
public class AppPracticeSessionService {

    private final PracticePersistencePort practicePersistence;
    private final AppTutoringMistakeService mistakeService;
    private final AppLearningService appLearningService;
    private final ObjectMapper objectMapper;
    private final AppPracticeVoAssembler practiceVoAssembler;

    public AppPracticeSessionService(PracticePersistencePort practicePersistence,
                                     AppTutoringMistakeService mistakeService,
                                     AppLearningService appLearningService,
                                     ObjectMapper objectMapper,
                                     AppPracticeVoAssembler practiceVoAssembler) {
        this.practicePersistence = practicePersistence;
        this.mistakeService = mistakeService;
        this.appLearningService = appLearningService;
        this.objectMapper = objectMapper;
        this.practiceVoAssembler = practiceVoAssembler;
    }

    public AppPracticeSessionVO createSession(AppPracticeCreateSessionRequest request) {
        Long userId = requireUserId();
        Long tenantId = TenantContext.currentTenantId();
        int questionCount = request.getQuestionCount() == null ? 2 : Math.min(request.getQuestionCount(), 30);
        boolean choice = request.getQuestionTypes() == null
                || Boolean.TRUE.equals(request.getQuestionTypes().get("choice"));
        boolean essay = request.getQuestionTypes() != null
                && Boolean.TRUE.equals(request.getQuestionTypes().get("essay"));
        List<AppPracticeQuestionVO> questions = PracticeQuestionFactory.buildQueue(questionCount, choice, essay);

        PracticeSessionEntity session = new PracticeSessionEntity();
        session.setTenantId(tenantId);
        session.setUserId(userId);
        session.setSessionCode("sess_" + System.currentTimeMillis());
        session.setSourceContentId(parseLong(request.getSourceId()));
        session.setTitle("综合练习");
        session.setQuestionCount(questions.size());
        session.setAnsweredCount(0);
        session.setStatus("IN_PROGRESS");
        session.setMode("MIXED");
        practicePersistence.saveSession(session);

        return practiceVoAssembler.toSessionVo(session, questions);
    }

    public AppPracticeChoiceResultVO submitChoice(String sessionCode, AppPracticeChoiceAnswerRequest request) {
        PracticeSessionEntity session = requireSession(sessionCode);
        int score = PracticeQuestionFactory.choiceScore(request.getAnswerKey());
        PracticeAnswerEntity answer = saveAnswer(session, request.getQuestionId(), "CHOICE",
                Map.of("answerKey", request.getAnswerKey()), score, null);
        afterAnswer(session, PracticeQuestionFactory.choiceScore(request.getAnswerKey()));
        return practiceVoAssembler.toChoiceResult(sessionCode, request.getAnswerKey(), answer);
    }

    public AppPracticeEssaySubmitVO submitEssay(String sessionCode, AppPracticeEssayAnswerRequest request) {
        PracticeSessionEntity session = requireSession(sessionCode);
        PracticeAnswerEntity answer = saveAnswer(session, request.getQuestionId(), "ESSAY",
                Map.of("answer", request.getAnswer()), null, null);
        answer.setStatus("PENDING_SCORE");
        practicePersistence.updateAnswer(answer);
        return practiceVoAssembler.toEssaySubmit(sessionCode, answer);
    }

    public int scoreEssayAnswer(Long answerId) {
        PracticeAnswerEntity answer = practicePersistence.findAnswerById(TenantContext.currentTenantId(), answerId)
                .orElseThrow(() -> notFound("作答记录不存在"));
        PracticeSessionEntity session = practicePersistence.findSessionById(
                TenantContext.currentTenantId(), requireUserId(), answer.getSessionId())
                .orElseThrow(() -> notFound("练习会话不存在"));
        int score = invokeScoring(answer);
        answer.setScore(score);
        answer.setStatus("SCORED");
        answer.setAiFeedbackJson(writeJson(Map.of("score", score, "comment", "AI 评分完成")));
        practicePersistence.updateAnswer(answer);
        afterAnswer(session, score);
        return score;
    }

    public AppPracticeDebriefVO debrief(String sessionCode) {
        PracticeSessionEntity session = requireSession(sessionCode);
        List<PracticeAnswerEntity> answers = practicePersistence.listAnswersBySession(
                TenantContext.currentTenantId(), session.getId());
        int correct = (int) answers.stream().filter(a -> a.getScore() != null && a.getScore() >= 60).count();
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", answers.size());
        summary.put("correct", correct);
        summary.put("accuracy", answers.isEmpty() ? 0 : Math.round(correct * 100.0 / answers.size()));
        return practiceVoAssembler.toDebrief(sessionCode, session, summary);
    }

    private void afterAnswer(PracticeSessionEntity session, int score) {
        session.setAnsweredCount(session.getAnsweredCount() + 1);
        if (session.getAnsweredCount() >= session.getQuestionCount()) {
            completeSession(session);
        } else {
            practicePersistence.updateSession(session);
        }
        if (score < 60) {
            mistakeService.saveFromPractice(requireUserId(), session.getSessionCode(),
                    "practice-" + UUID.randomUUID(), session.getSourceContentId(),
                    session.getTitle(), "民法", score, "练习得分低于 60 分");
            enqueueReview(session, score);
        }
    }

    private void completeSession(PracticeSessionEntity session) {
        List<PracticeAnswerEntity> answers = practicePersistence.listAnswersBySession(
                TenantContext.currentTenantId(), session.getId());
        int scored = (int) answers.stream().filter(a -> a.getScore() != null).count();
        int correct = (int) answers.stream().filter(a -> a.getScore() != null && a.getScore() >= 60).count();
        int accuracy = scored == 0 ? 0 : Math.round(correct * 100f / scored);
        session.setAccuracy(accuracy);
        session.setMinutes(Math.max(1, (int) ChronoUnit.MINUTES.between(session.getCreateTime(), LocalDateTime.now())));
        session.setStatus("COMPLETED");
        practicePersistence.updateSession(session);
    }

    private void enqueueReview(PracticeSessionEntity session, int score) {
        ReviewPendingEntity pending = new ReviewPendingEntity();
        pending.setTenantId(session.getTenantId());
        pending.setUserId(session.getUserId());
        pending.setContentId(session.getSourceContentId());
        pending.setTitle(session.getTitle());
        pending.setTag("民法");
        pending.setAccuracy(score);
        pending.setDueAt(LocalDateTime.now().minusDays(1));
        pending.setUrgency("OVERDUE");
        pending.setStatus("OPEN");
        practicePersistence.saveReviewPending(pending);
    }

    private int invokeScoring(PracticeAnswerEntity answer) {
        try {
            AppLearningInvokeRequest request = new AppLearningInvokeRequest();
            request.setMode("SCORING");
            request.setInput(Map.of("answer", readPayload(answer)));
            CapabilityExecuteResponse response = appLearningService.invoke(request);
            if (response != null && response.result() != null
                    && "SUCCESS".equals(response.result().status())) {
                return 75;
            }
        } catch (ServiceException ex) {
            if (PlatformErrorCode.RUNTIME_QUOTA_EXCEEDED.getCode().equals(ex.getCode())) {
                throw ex;
            }
        } catch (Exception ignored) {
            // 降级默认分
        }
        return 70;
    }

    private PracticeAnswerEntity saveAnswer(PracticeSessionEntity session, String questionId, String type,
                                            Map<String, Object> payload, Integer score, String feedback) {
        PracticeAnswerEntity answer = new PracticeAnswerEntity();
        answer.setTenantId(session.getTenantId());
        answer.setSessionId(session.getId());
        answer.setQuestionId(questionId);
        answer.setQuestionType(type);
        answer.setAnswerPayloadJson(writeJson(payload));
        answer.setScore(score);
        answer.setAiFeedbackJson(feedback);
        answer.setStatus("SUBMITTED");
        return practicePersistence.saveAnswer(answer);
    }

    private PracticeSessionEntity requireSession(String sessionCode) {
        return practicePersistence.findSessionByCode(
                TenantContext.currentTenantId(), requireUserId(), sessionCode)
                .orElseThrow(() -> notFound("练习会话不存在"));
    }

    private Long requireUserId() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            throw Errors.of(PlatformErrorCode.UNAUTHORIZED);
        }
        return userId;
    }

    private ServiceException notFound(String message) {
        return Errors.of(PlatformErrorCode.NOT_FOUND, message);
    }

    private Long parseLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String readPayload(PracticeAnswerEntity answer) {
        try {
            Map<?, ?> map = objectMapper.readValue(answer.getAnswerPayloadJson(), Map.class);
            Object text = map.get("answer");
            return text == null ? "" : String.valueOf(text);
        } catch (Exception ex) {
            return "";
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
