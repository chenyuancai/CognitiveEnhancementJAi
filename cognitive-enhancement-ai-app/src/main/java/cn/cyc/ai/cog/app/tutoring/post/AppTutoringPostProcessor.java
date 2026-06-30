package cn.cyc.ai.cog.app.tutoring.post;

import cn.cyc.ai.cog.app.tutoring.context.AppTutoringResolvedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringGovernanceResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringLlmAnalysisResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.app.tutoring.profile.AppTutoringProfileUpdater;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringIntent;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;
import cn.cyc.ai.cog.platform.tutoring.entity.MessageReferenceEntity;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅导轮次后处理器：负责画像刷新、错题记录、学习计划、练习推荐与引用审计。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringPostProcessor {

    /** 错题服务。 */
    private final cn.cyc.ai.cog.app.tutoring.service.AppTutoringMistakeService mistakeService;

    /** 学习计划服务。 */
    private final cn.cyc.ai.cog.app.tutoring.service.AppTutoringLearningPlanService learningPlanService;

    /** 练习推荐服务。 */
    private final cn.cyc.ai.cog.app.tutoring.service.AppTutoringPracticeService practiceService;

    /** 学习画像更新器。 */
    private final AppTutoringProfileUpdater profileUpdater;

    /** 会话标题服务。 */
    private final cn.cyc.ai.cog.app.tutoring.service.AppTutoringSessionTitleService sessionTitleService;

    /** 辅导持久化服务提供者。 */
    private final ObjectProvider<TutoringPersistenceService> persistenceProvider;

    /**
     * 构造轮次后处理器。
     *
     * @param mistakeService       错题服务
     * @param learningPlanService  学习计划服务
     * @param practiceService      练习推荐服务
     * @param profileUpdater       学习画像更新器
     * @param sessionTitleService  会话标题服务
     * @param persistenceProvider  辅导持久化服务提供者
     */
    public AppTutoringPostProcessor(cn.cyc.ai.cog.app.tutoring.service.AppTutoringMistakeService mistakeService,
                                    cn.cyc.ai.cog.app.tutoring.service.AppTutoringLearningPlanService learningPlanService,
                                    cn.cyc.ai.cog.app.tutoring.service.AppTutoringPracticeService practiceService,
                                    AppTutoringProfileUpdater profileUpdater,
                                    cn.cyc.ai.cog.app.tutoring.service.AppTutoringSessionTitleService sessionTitleService,
                                    ObjectProvider<TutoringPersistenceService> persistenceProvider) {
        this.mistakeService = mistakeService;
        this.learningPlanService = learningPlanService;
        this.practiceService = practiceService;
        this.profileUpdater = profileUpdater;
        this.sessionTitleService = sessionTitleService;
        this.persistenceProvider = persistenceProvider;
    }

    /**
     * 执行单轮对话结束后的异步后处理逻辑。
     *
     * @param context 轮次后处理上下文
     */
    public void afterTurn(AppTutoringAfterTurnContext context) {
        Long userId = context.userId();
        if (userId == null) {
            return;
        }
        profileUpdater.refresh(userId, context.studentState(), context.decision());
        saveReferences(context);
        if (context.decision().intent() == AppTutoringIntent.MISTAKE_ANALYSIS) {
            mistakeService.saveIfNeeded(
                    userId,
                    context.sessionId(),
                    context.traceId(),
                    resolveKnowledgePoint(context),
                    resolveMistakeSummary(context),
                    context.message(),
                    context.answer());
        }
        if (context.decision().intent() == AppTutoringIntent.LEARNING_PLAN) {
            Long planId = learningPlanService.savePlan(
                    userId, context.sessionId(), context.traceId(), context.blueprint());
            if (planId != null) {
                profileUpdater.updateActivePlan(userId, planId);
            }
        }
        if ("PRACTICE_CHECK".equals(context.decision().nextActionType())) {
            practiceService.recommend(
                    userId,
                    context.sessionId(),
                    context.traceId(),
                    context.studentState().getKnowledgePoint(),
                    buildPracticePrompt(context));
        }
        if (context.newSession()) {
            sessionTitleService.rewriteIfEnabled(context.sessionId(), context.message(), context.answer());
        }
    }

    /**
     * 持久化本轮引用的消息、知识与文件片段。
     *
     * @param context 轮次后处理上下文
     */
    private void saveReferences(AppTutoringAfterTurnContext context) {
        TutoringPersistenceService persistence = persistenceProvider.getIfAvailable();
        if (persistence == null || context.resolvedContext() == null) {
            return;
        }
        List<MessageReferenceEntity> references = new ArrayList<>();
        AppTutoringResolvedContext resolved = context.resolvedContext();
        if (!CollectionUtils.isEmpty(resolved.getMessageSnippets())) {
            resolved.getMessageSnippets().forEach(snippet -> {
                MessageReferenceEntity entity = new MessageReferenceEntity();
                entity.setRefType("MESSAGE");
                entity.setRefId(snippet.getMessageId());
                entity.setExcerpt(snippet.getContent());
                references.add(entity);
            });
        }
        if (!CollectionUtils.isEmpty(resolved.getKnowledgeSnippets())) {
            resolved.getKnowledgeSnippets().forEach(snippet -> {
                MessageReferenceEntity entity = new MessageReferenceEntity();
                entity.setRefType("KNOWLEDGE");
                entity.setRefId(snippet.getKnowledgeId());
                entity.setExcerpt(snippet.getExcerpt());
                references.add(entity);
            });
        }
        if (!CollectionUtils.isEmpty(resolved.getFileSnippets())) {
            resolved.getFileSnippets().forEach(snippet -> {
                MessageReferenceEntity entity = new MessageReferenceEntity();
                entity.setRefType("FILE");
                entity.setRefId(snippet.getFileId());
                entity.setExcerpt(snippet.getExcerpt());
                references.add(entity);
            });
        }
        if (references.isEmpty()) {
            return;
        }
        persistence.saveMessageReferences(
                context.sessionId(), context.traceId(), context.messageId(), references);
    }

    /**
     * 解析错题关联的知识点。
     *
     * @param context 轮次后处理上下文
     * @return 知识点名称
     */
    private String resolveKnowledgePoint(AppTutoringAfterTurnContext context) {
        if (StringUtils.hasText(context.llmAnalysis().knowledgePoint())) {
            return context.llmAnalysis().knowledgePoint();
        }
        return context.studentState().getKnowledgePoint();
    }

    /**
     * 解析错题摘要描述。
     *
     * @param context 轮次后处理上下文
     * @return 错题摘要
     */
    private String resolveMistakeSummary(AppTutoringAfterTurnContext context) {
        if (StringUtils.hasText(context.llmAnalysis().mistakeSummary())) {
            return context.llmAnalysis().mistakeSummary();
        }
        return abbreviate(context.message());
    }

    /**
     * 构建练习推荐 Prompt。
     *
     * @param context 轮次后处理上下文
     * @return 练习 Prompt 文本
     */
    private String buildPracticePrompt(AppTutoringAfterTurnContext context) {
        String topic = context.studentState().getKnowledgePoint();
        if (!StringUtils.hasText(topic)) {
            topic = "当前知识点";
        }
        return "请完成一道关于「" + topic + "」的小练习，并说明你的思路。";
    }

    /**
     * 截断文本用于摘要存储。
     *
     * @param text 原始文本
     * @return 截断后的文本
     */
    private String abbreviate(String text) {
        String compact = text == null ? "" : text.trim();
        return compact.length() <= 80 ? compact : compact.substring(0, 80);
    }
}
