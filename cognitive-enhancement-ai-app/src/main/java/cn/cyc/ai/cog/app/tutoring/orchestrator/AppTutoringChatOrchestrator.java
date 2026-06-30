package cn.cyc.ai.cog.app.tutoring.orchestrator;

import cn.cyc.ai.cog.app.tutoring.access.AppTutoringAccessGate;
import cn.cyc.ai.cog.app.tutoring.access.AppTutoringKnowledgeAccessValidator;
import cn.cyc.ai.cog.app.tutoring.analyze.AppTutoringLlmAnalyzer;
import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringHotHistoryCache;
import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringContextLoader;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringInputPreprocessor;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringLoadedContext;
import cn.cyc.ai.cog.app.tutoring.context.AppTutoringResolvedContext;
import cn.cyc.ai.cog.app.tutoring.dto.AppLearningProfile;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringBlueprint;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatRequest;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatResponse;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringChatState;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringGovernanceResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringLlmAnalysisResult;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringMessageVO;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStreamEvent;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.app.tutoring.govern.AppTutoringOutputGovernor;
import cn.cyc.ai.cog.app.tutoring.post.AppTutoringAfterTurnContext;
import cn.cyc.ai.cog.app.tutoring.post.AppTutoringPostProcessor;
import cn.cyc.ai.cog.app.tutoring.profile.AppTutoringMasteryAggregator;
import cn.cyc.ai.cog.app.tutoring.prompt.AppTutoringPromptBuilder;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringLearningPlanService;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringLearningStateService;
import cn.cyc.ai.cog.app.tutoring.profile.AppTutoringProfileLoader;
import cn.cyc.ai.cog.app.tutoring.service.AppTutoringSummaryService;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringBlueprintBuilder;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecider;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyDecision;
import cn.cyc.ai.cog.app.tutoring.strategy.AppTutoringStrategyInput;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringConstants;
import cn.cyc.ai.cog.app.tutoring.support.AppTutoringTenantSync;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import cn.cyc.ai.cog.runtime.session.domain.ConversationSession;
import cn.cyc.ai.cog.runtime.session.domain.MessageRole;
import cn.cyc.ai.cog.runtime.session.service.ConversationSessionService;
import cn.cyc.ai.cog.runtime.session.spi.ConversationMessageRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * C 端 AI 学习辅导对话编排器，串联访问控制、上下文加载、策略决策、模型调用与后处理全流程。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringChatOrchestrator {

    /** 访问门禁组件。 */
    private final AppTutoringAccessGate accessGate;

    /** 知识引用权限校验器。 */
    private final AppTutoringKnowledgeAccessValidator knowledgeAccessValidator;

    /** 会话服务。 */
    private final ConversationSessionService conversationSessionService;

    /** 会话消息仓储。 */
    private final ConversationMessageRepository conversationMessageRepository;

    /** 热历史 Redis 缓存。 */
    private final AppTutoringHotHistoryCache hotHistoryCache;

    /** 引用上下文预处理器。 */
    private final AppTutoringInputPreprocessor inputPreprocessor;

    /** 会话摘要服务。 */
    private final AppTutoringSummaryService summaryService;

    /** 学习状态服务。 */
    private final AppTutoringLearningStateService learningStateService;

    /** 学习画像加载器。 */
    private final AppTutoringProfileLoader profileLoader;

    /** 学习计划服务。 */
    private final AppTutoringLearningPlanService learningPlanService;

    /** 掌握度聚合器。 */
    private final AppTutoringMasteryAggregator masteryAggregator;

    /** LLM 分析器。 */
    private final AppTutoringLlmAnalyzer llmAnalyzer;

    /** 上下文加载器。 */
    private final AppTutoringContextLoader contextLoader;

    /** 教学策略决策器。 */
    private final AppTutoringStrategyDecider strategyDecider;

    /** 教学蓝图组装器。 */
    private final AppTutoringBlueprintBuilder blueprintBuilder;

    /** Prompt 组装器。 */
    private final AppTutoringPromptBuilder promptBuilder;

    /** 输出治理器。 */
    private final AppTutoringOutputGovernor outputGovernor;

    /** 轮次后处理器。 */
    private final AppTutoringPostProcessor postProcessor;

    /** 运行时能力执行器。 */
    private final RuntimeHarness runtimeHarness;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /** 辅导持久化服务提供者。 */
    private final ObjectProvider<TutoringPersistenceService> tutoringPersistenceServiceProvider;

    /**
     * 构造对话编排器。
     *
     * @param accessGate                          访问门禁组件
     * @param knowledgeAccessValidator            知识引用权限校验器
     * @param conversationSessionService          会话服务
     * @param conversationMessageRepository       会话消息仓储
     * @param hotHistoryCache                     热历史缓存
     * @param inputPreprocessor                   引用上下文预处理器
     * @param summaryService                      会话摘要服务
     * @param learningStateService                学习状态服务
     * @param profileLoader                       学习画像加载器
     * @param learningPlanService                 学习计划服务
     * @param masteryAggregator                   掌握度聚合器
     * @param llmAnalyzer                         LLM 分析器
     * @param contextLoader                       上下文加载器
     * @param strategyDecider                     教学策略决策器
     * @param blueprintBuilder                    教学蓝图组装器
     * @param promptBuilder                       Prompt 组装器
     * @param outputGovernor                      输出治理器
     * @param postProcessor                       轮次后处理器
     * @param runtimeHarness                      运行时能力执行器
     * @param properties                          学习辅导配置属性
     * @param tutoringPersistenceServiceProvider  辅导持久化服务提供者
     */
    public AppTutoringChatOrchestrator(AppTutoringAccessGate accessGate,
                                       AppTutoringKnowledgeAccessValidator knowledgeAccessValidator,
                                       ConversationSessionService conversationSessionService,
                                       ConversationMessageRepository conversationMessageRepository,
                                       AppTutoringHotHistoryCache hotHistoryCache,
                                       AppTutoringInputPreprocessor inputPreprocessor,
                                       AppTutoringSummaryService summaryService,
                                       AppTutoringLearningStateService learningStateService,
                                       AppTutoringProfileLoader profileLoader,
                                       AppTutoringLearningPlanService learningPlanService,
                                       AppTutoringMasteryAggregator masteryAggregator,
                                       AppTutoringLlmAnalyzer llmAnalyzer,
                                       AppTutoringContextLoader contextLoader,
                                       AppTutoringStrategyDecider strategyDecider,
                                       AppTutoringBlueprintBuilder blueprintBuilder,
                                       AppTutoringPromptBuilder promptBuilder,
                                       AppTutoringOutputGovernor outputGovernor,
                                       AppTutoringPostProcessor postProcessor,
                                       RuntimeHarness runtimeHarness,
                                       AppTutoringProperties properties,
                                       ObjectProvider<TutoringPersistenceService> tutoringPersistenceServiceProvider) {
        this.accessGate = accessGate;
        this.knowledgeAccessValidator = knowledgeAccessValidator;
        this.conversationSessionService = conversationSessionService;
        this.conversationMessageRepository = conversationMessageRepository;
        this.hotHistoryCache = hotHistoryCache;
        this.inputPreprocessor = inputPreprocessor;
        this.summaryService = summaryService;
        this.learningStateService = learningStateService;
        this.profileLoader = profileLoader;
        this.learningPlanService = learningPlanService;
        this.masteryAggregator = masteryAggregator;
        this.llmAnalyzer = llmAnalyzer;
        this.contextLoader = contextLoader;
        this.strategyDecider = strategyDecider;
        this.blueprintBuilder = blueprintBuilder;
        this.promptBuilder = promptBuilder;
        this.outputGovernor = outputGovernor;
        this.postProcessor = postProcessor;
        this.runtimeHarness = runtimeHarness;
        this.properties = properties;
        this.tutoringPersistenceServiceProvider = tutoringPersistenceServiceProvider;
    }

    /**
     * 执行同步聊天请求。
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    public AppTutoringChatResponse chat(AppTutoringChatRequest request) {
        return AppTutoringTenantSync.runWithRuntimeTenant(() -> execute(request, null, null));
    }

    /**
     * 执行流式聊天请求并推送阶段事件。
     *
     * @param request       聊天请求
     * @param eventConsumer 阶段事件消费者
     * @return 聊天响应
     */
    public AppTutoringChatResponse chatStream(AppTutoringChatRequest request,
                                              Consumer<AppTutoringStreamEvent> eventConsumer) {
        return chatStream(request, null, eventConsumer);
    }

    /**
     * 执行流式聊天请求，支持预设 Trace ID。
     *
     * @param request       聊天请求
     * @param presetTraceId 预设追踪 ID
     * @param eventConsumer 阶段事件消费者
     * @return 聊天响应
     */
    public AppTutoringChatResponse chatStream(AppTutoringChatRequest request,
                                              String presetTraceId,
                                              Consumer<AppTutoringStreamEvent> eventConsumer) {
        return AppTutoringTenantSync.runWithRuntimeTenant(() -> execute(request, presetTraceId, eventConsumer));
    }

    /**
     * 查询会话消息列表。
     *
     * @param sessionId 会话 ID
     * @return 消息视图对象列表
     */
    public List<AppTutoringMessageVO> listMessages(String sessionId) {
        return AppTutoringTenantSync.runWithRuntimeTenant(() ->
                conversationSessionService.listMessages(sessionId).stream()
                        .map(this::toMessageVO)
                        .toList());
    }

    /**
     * 执行单轮辅导对话的核心编排逻辑。
     *
     * @param request       聊天请求
     * @param presetTraceId 预设追踪 ID
     * @param eventConsumer 阶段事件消费者
     * @return 聊天响应
     */
    private AppTutoringChatResponse execute(AppTutoringChatRequest request,
                                            String presetTraceId,
                                            Consumer<AppTutoringStreamEvent> eventConsumer) {
        accessGate.checkTutoringAllowed();
        knowledgeAccessValidator.validate(request.getReferences());

        Long userId = UserContext.currentUserId();
        AppLearningProfile profile = profileLoader.loadForCurrentUser();
        emit(eventConsumer, "PROFILE_LOADED", null, request.getSessionId(), Map.of(
                "overallMastery", profile.getOverallMastery().name(),
                "weakTopicCount", profile.getWeakTopics().size()));

        String message = request.getMessage().trim();
        boolean newSession = !StringUtils.hasText(request.getSessionId());
        String sessionCapabilityCode = resolveSessionCapabilityCode(request);
        ConversationSession session = resolveSession(request, message, sessionCapabilityCode);
        String traceId = StringUtils.hasText(presetTraceId) ? presetTraceId : UUID.randomUUID().toString();

        conversationSessionService.appendMessage(session.sessionId(), MessageRole.USER, message, traceId);
        List<ConversationMessage> currentMessages = conversationMessageRepository.findBySessionId(session.sessionId());
        hotHistoryCache.refreshSafely(session.sessionId(), currentMessages);

        AppTutoringResolvedContext resolvedContext =
                inputPreprocessor.resolve(request.getReferences(), currentMessages);
        String sessionSummary = summaryService.loadSummary(session.sessionId());
        AppTutoringLoadedContext loadedContext = contextLoader.load(
                session.sessionId(), currentMessages, sessionSummary, resolvedContext);
        emit(eventConsumer, "CONTEXT_LOADED", traceId, session.sessionId(), Map.of(
                "loadedFromRedis", loadedContext.loadedFromRedis(),
                "loadedFromDb", loadedContext.loadedFromDb(),
                "recentMessageCount", loadedContext.recentMessages().size(),
                "hasSummary", StringUtils.hasText(loadedContext.sessionSummary())));

        AppTutoringLlmAnalysisResult llmAnalysis =
                llmAnalyzer.analyzeIfEnabled(message, loadedContext, profile);
        AppTutoringStudentState studentState = learningStateService.prepareForDecision(
                session.sessionId(), message, loadedContext.recentMessages());
        studentState = masteryAggregator.mergeSessionWithProfile(studentState, profile);

        AppTutoringStrategyDecision decision = strategyDecider.decide(new AppTutoringStrategyInput(
                message,
                loadedContext.recentMessages(),
                studentState,
                resolvedContext,
                properties.getStuckFallbackThreshold(),
                profile,
                llmAnalysis));
        emit(eventConsumer, "STRATEGY_SELECTED", traceId, session.sessionId(), Map.of(
                "intent", decision.intent().name(),
                "strategy", decision.strategy().name(),
                "reason", decision.reason(),
                "stuckCount", studentState.getStuckCount()));

        AppTutoringBlueprint blueprint = blueprintBuilder.build(
                request, session.sessionId(), traceId, decision, studentState, loadedContext);
        emit(eventConsumer, "BLUEPRINT_READY", traceId, session.sessionId(), Map.of(
                "intent", blueprint.getIntent(),
                "strategy", blueprint.getSelectedStrategy(),
                "nextAction", blueprint.getNextAction().getType()));

        String activePlanJson = learningPlanService.buildPlanPromptSection();
        String prompt = promptBuilder.build(
                message, blueprint, loadedContext, profile, activePlanJson);
        CapabilityExecuteResponse llmResponse = runtimeHarness.execute(new CapabilityExecuteRequest(
                properties.getRuntimeCapabilityCode(),
                Map.of("question", prompt),
                Map.of(
                        "sessionId", session.sessionId(),
                        "conversationEnabled", false,
                        "teachingStrategy", decision.strategy().name(),
                        "tutoringIntent", decision.intent().name())));

        String answer = extractAnswer(llmResponse);
        AppTutoringGovernanceResult governed =
                outputGovernor.govern(answer, blueprint, decision);
        answer = governed.getAnswer();
        if (governed.isViolated()) {
            emit(eventConsumer, "GOVERNANCE_APPLIED", traceId, session.sessionId(), Map.of(
                    "violations", governed.getViolations()));
        }

        String responseTraceId = StringUtils.hasText(llmResponse.traceId()) ? llmResponse.traceId() : traceId;
        ConversationMessage assistantMessage = conversationSessionService.appendMessage(
                session.sessionId(), MessageRole.ASSISTANT, answer, responseTraceId);
        List<ConversationMessage> allMessages =
                conversationMessageRepository.findBySessionId(session.sessionId());
        hotHistoryCache.refreshSafely(session.sessionId(), allMessages);
        summaryService.refreshIfNeeded(session.sessionId(), allMessages);
        learningStateService.save(session.sessionId(), responseTraceId, studentState, decision);
        persistBlueprint(session.sessionId(), responseTraceId, assistantMessage.messageId(), decision, blueprint);

        postProcessor.afterTurn(new AppTutoringAfterTurnContext(
                userId,
                session.sessionId(),
                responseTraceId,
                assistantMessage.messageId(),
                message,
                answer,
                newSession,
                studentState,
                decision,
                blueprint,
                resolvedContext,
                llmAnalysis,
                governed));

        blueprint.setTraceId(responseTraceId);
        AppTutoringChatResponse response = buildResponse(
                session.sessionId(), assistantMessage, decision, blueprint, answer, studentState);
        emit(eventConsumer, "COMPLETED", responseTraceId, session.sessionId(), Map.of(
                "messageId", assistantMessage.messageId(),
                "intent", response.getIntent(),
                "strategy", response.getStrategy(),
                "needUserReply", response.isNeedUserReply()));
        return response;
    }

    /**
     * 持久化本轮教学蓝图。
     *
     * @param sessionId  会话 ID
     * @param traceId    追踪 ID
     * @param messageId  助手消息 ID
     * @param decision   策略决策结果
     * @param blueprint  教学蓝图
     */
    private void persistBlueprint(String sessionId,
                                  String traceId,
                                  String messageId,
                                  AppTutoringStrategyDecision decision,
                                  AppTutoringBlueprint blueprint) {
        TutoringPersistenceService persistence = tutoringPersistenceServiceProvider.getIfAvailable();
        if (persistence == null) {
            return;
        }
        persistence.saveBlueprint(
                sessionId,
                traceId,
                messageId,
                decision.intent().name(),
                decision.strategy().name(),
                blueprint);
    }

    /**
     * 向事件消费者发送阶段事件。
     *
     * @param eventConsumer 阶段事件消费者
     * @param type          事件类型
     * @param traceId       追踪 ID
     * @param sessionId     会话 ID
     * @param payload       事件负载
     */
    private void emit(Consumer<AppTutoringStreamEvent> eventConsumer,
                      String type,
                      String traceId,
                      String sessionId,
                      Map<String, Object> payload) {
        if (eventConsumer == null) {
            return;
        }
        eventConsumer.accept(AppTutoringStreamEvent.of(type, traceId, sessionId, payload));
    }

    /**
     * 解析或创建会话。
     *
     * @param request         聊天请求
     * @param message         用户消息（用于新会话标题）
     * @param capabilityCode  会话能力编码
     * @return 会话对象
     */
    private ConversationSession resolveSession(AppTutoringChatRequest request,
                                               String message,
                                               String capabilityCode) {
        if (StringUtils.hasText(request.getSessionId())) {
            return conversationSessionService.getSession(request.getSessionId());
        }
        String userId = UserContext.currentUserId() == null ? null : String.valueOf(UserContext.currentUserId());
        return conversationSessionService.createSession(userId, capabilityCode, rewriteTitle(message));
    }

    /**
     * 解析会话能力编码，请求未指定时使用默认值。
     *
     * @param request 聊天请求
     * @return 会话能力编码
     */
    private String resolveSessionCapabilityCode(AppTutoringChatRequest request) {
        if (StringUtils.hasText(request.getCapabilityCode())) {
            return request.getCapabilityCode().trim();
        }
        return AppTutoringConstants.SESSION_CAPABILITY_CODE;
    }

    /**
     * 将用户首条消息截断为会话标题。
     *
     * @param message 用户消息
     * @return 会话标题
     */
    private String rewriteTitle(String message) {
        String compact = message.replaceAll("\\s+", " ").trim();
        if (compact.length() <= 20) {
            return compact;
        }
        return compact.substring(0, 20);
    }

    /**
     * 从能力执行响应中提取助手回答文本。
     *
     * @param response 能力执行响应
     * @return 回答文本
     */
    private String extractAnswer(CapabilityExecuteResponse response) {
        if (response == null || response.result() == null) {
            throw Errors.of(PlatformErrorCode.SERVICE_UNAVAILABLE, "AI 助手未返回结果");
        }
        ExecutionResult result = response.result();
        if (StringUtils.hasText(result.message())) {
            return result.message();
        }
        Object businessOutput = result.output().get("businessOutput");
        if (businessOutput != null && StringUtils.hasText(String.valueOf(businessOutput))) {
            return String.valueOf(businessOutput);
        }
        return result.output().toString();
    }

    /**
     * 组装聊天响应对象。
     *
     * @param sessionId         会话 ID
     * @param assistantMessage  助手消息
     * @param decision          策略决策结果
     * @param blueprint         教学蓝图
     * @param answer            助手回答
     * @param studentState      学生学习状态
     * @return 聊天响应
     */
    private AppTutoringChatResponse buildResponse(String sessionId,
                                                  ConversationMessage assistantMessage,
                                                  AppTutoringStrategyDecision decision,
                                                  AppTutoringBlueprint blueprint,
                                                  String answer,
                                                  AppTutoringStudentState studentState) {
        AppTutoringChatState state = new AppTutoringChatState();
        state.setNeedUserReply(decision.needUserReply());
        state.setNextExpectedAction(resolveNextExpectedAction(decision.nextActionType()));
        state.setStuckCount(studentState.getStuckCount());
        state.setMasteryLevel(studentState.getMasteryLevel());

        AppTutoringChatResponse response = new AppTutoringChatResponse();
        response.setSessionId(sessionId);
        response.setTraceId(blueprint.getTraceId());
        response.setMessageId(assistantMessage.messageId());
        response.setIntent(decision.intent().name());
        response.setStrategy(decision.strategy().name());
        response.setAnswer(answer);
        response.setNeedUserReply(decision.needUserReply());
        response.setBlueprint(blueprint);
        response.setState(state);
        return response;
    }

    /**
     * 将下一步动作类型映射为客户端期望动作编码。
     *
     * @param nextActionType 下一步动作类型
     * @return 客户端期望动作编码
     */
    private String resolveNextExpectedAction(String nextActionType) {
        if ("ASK_GUIDING_QUESTION".equals(nextActionType)) {
            return "ANSWER_GUIDING_QUESTION";
        }
        if ("FINAL_ANSWER".equals(nextActionType) || "SUMMARY_REVIEW".equals(nextActionType)
                || "LEARNING_PLAN".equals(nextActionType)) {
            return "NONE";
        }
        if ("PRACTICE_CHECK".equals(nextActionType)) {
            return "ANSWER_PRACTICE";
        }
        return "WAIT_USER_INPUT";
    }

    /**
     * 将领域消息对象转换为视图对象。
     *
     * @param message 领域消息
     * @return 消息视图对象
     */
    private AppTutoringMessageVO toMessageVO(ConversationMessage message) {
        AppTutoringMessageVO vo = new AppTutoringMessageVO();
        vo.setMessageId(message.messageId());
        vo.setRole(message.role().name());
        vo.setContent(message.content());
        vo.setTraceId(message.traceId());
        vo.setRecordedAt(message.recordedAt());
        return vo;
    }
}
