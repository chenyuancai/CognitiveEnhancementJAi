package cn.cyc.ai.cog.app.tutoring.service;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringCachedMessage;
import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringSessionSummaryCache;
import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.core.harness.RuntimeHarness;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteRequest;
import cn.cyc.ai.cog.core.runtime.CapabilityExecuteResponse;
import cn.cyc.ai.cog.core.runtime.ExecutionResult;
import cn.cyc.ai.cog.platform.tutoring.service.TutoringPersistenceService;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会话摘要服务，支持规则压缩与 LLM 摘要长对话上下文。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class AppTutoringSummaryService {

    /**
     * 日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(AppTutoringSummaryService.class);

    /**
     * 会话摘要缓存。
     */
    private final AppTutoringSessionSummaryCache summaryCache;

    /**
     * 学习辅导配置属性。
     */
    private final AppTutoringProperties properties;

    /**
     * 学习辅导持久化服务提供者。
     */
    private final ObjectProvider<TutoringPersistenceService> tutoringPersistenceServiceProvider;

    /**
     * 运行时能力执行器提供者。
     */
    private final ObjectProvider<RuntimeHarness> runtimeHarnessProvider;

    /**
     * 创建会话摘要服务。
     *
     * @param summaryCache                        会话摘要缓存
     * @param properties                          学习辅导配置属性
     * @param tutoringPersistenceServiceProvider  学习辅导持久化服务提供者
     * @param runtimeHarnessProvider              运行时能力执行器提供者
     */
    public AppTutoringSummaryService(AppTutoringSessionSummaryCache summaryCache,
                                     AppTutoringProperties properties,
                                     ObjectProvider<TutoringPersistenceService> tutoringPersistenceServiceProvider,
                                     ObjectProvider<RuntimeHarness> runtimeHarnessProvider) {
        this.summaryCache = summaryCache;
        this.properties = properties;
        this.tutoringPersistenceServiceProvider = tutoringPersistenceServiceProvider;
        this.runtimeHarnessProvider = runtimeHarnessProvider;
    }

    /**
     * 加载会话摘要，优先读缓存，其次读数据库。
     *
     * @param sessionId 会话 ID
     * @return 摘要文本，不存在时返回 null
     */
    public String loadSummary(String sessionId) {
        return summaryCache.loadSafely(sessionId)
                .or(() -> {
                    TutoringPersistenceService persistence = tutoringPersistenceServiceProvider.getIfAvailable();
                    if (persistence == null) {
                        return java.util.Optional.empty();
                    }
                    String dbSummary = persistence.findConversationSummary(sessionId);
                    if (StringUtils.hasText(dbSummary)) {
                        summaryCache.saveSafely(sessionId, dbSummary);
                    }
                    return java.util.Optional.ofNullable(dbSummary);
                })
                .orElse(null);
    }

    /**
     * 按消息量判断是否需要刷新会话摘要。
     *
     * @param sessionId 会话 ID
     * @param messages  当前会话消息列表
     * @return 最新摘要文本
     */
    public String refreshIfNeeded(String sessionId, List<ConversationMessage> messages) {
        return refreshWithLlmIfEnabled(sessionId, messages);
    }

    /**
     * 按消息量刷新摘要；开启 LLM 摘要时优先调用模型，失败回退规则压缩。
     *
     * @param sessionId 会话 ID
     * @param messages  当前会话消息列表
     * @return 刷新后的摘要文本
     */
    public String refreshWithLlmIfEnabled(String sessionId, List<ConversationMessage> messages) {
        if (messages.size() < properties.getSummaryTriggerMessageCount()) {
            return loadSummary(sessionId);
        }
        String summary = properties.isLlmSummaryEnabled()
                ? buildLlmSummary(messages)
                : null;
        if (!StringUtils.hasText(summary)) {
            summary = buildRuleSummary(messages);
        }
        summaryCache.saveSafely(sessionId, summary);
        TutoringPersistenceService persistence = tutoringPersistenceServiceProvider.getIfAvailable();
        if (persistence != null) {
            persistence.upsertConversationSummary(sessionId, summary);
        }
        return summary;
    }

    /**
     * 调用 LLM 能力压缩较早的对话消息为摘要。
     *
     * @param messages 会话消息列表
     * @return 摘要文本，失败时返回 null
     */
    private String buildLlmSummary(List<ConversationMessage> messages) {
        RuntimeHarness runtimeHarness = runtimeHarnessProvider.getIfAvailable();
        if (runtimeHarness == null) {
            return null;
        }
        try {
            List<ConversationMessage> sorted = messages.stream()
                    .sorted(Comparator.comparing(ConversationMessage::recordedAt))
                    .toList();
            int keep = Math.min(properties.getSummaryKeepRecentMessages(), sorted.size());
            List<ConversationMessage> older = sorted.subList(0, Math.max(0, sorted.size() - keep));
            if (older.isEmpty()) {
                return null;
            }
            String transcript = older.stream()
                    .map(message -> message.role().name() + ": " + abbreviate(message.content()))
                    .collect(Collectors.joining("\n"));
            String prompt = """
                    请将以下学习辅导对话压缩为不超过200字的中文摘要，保留知识点与卡点，只输出摘要正文。
                    %s
                    """.formatted(transcript);
            CapabilityExecuteResponse response = runtimeHarness.execute(new CapabilityExecuteRequest(
                    properties.getAnalyzeCapabilityCode(),
                    Map.of("question", prompt),
                    Map.of("conversationEnabled", false, "summaryMode", true)));
            String summary = extractText(response).trim();
            return StringUtils.hasText(summary) ? summary : null;
        } catch (Exception ex) {
            log.warn("tutoring llm summary failed, fallback to rules", ex);
            return null;
        }
    }

    /**
     * 从能力执行响应中提取文本输出。
     *
     * @param response 能力执行响应
     * @return 文本内容
     */
    private String extractText(CapabilityExecuteResponse response) {
        if (response == null || response.result() == null) {
            return "";
        }
        ExecutionResult result = response.result();
        if (StringUtils.hasText(result.message())) {
            return result.message();
        }
        Object businessOutput = result.output().get("businessOutput");
        return businessOutput == null ? "" : String.valueOf(businessOutput);
    }

    /**
     * 使用规则拼接较早消息生成摘要。
     *
     * @param messages 会话消息列表
     * @return 规则压缩摘要
     */
    private String buildRuleSummary(List<ConversationMessage> messages) {
        List<ConversationMessage> sorted = messages.stream()
                .sorted(Comparator.comparing(ConversationMessage::recordedAt))
                .toList();
        int keep = Math.min(properties.getSummaryKeepRecentMessages(), sorted.size());
        List<ConversationMessage> older = sorted.subList(0, Math.max(0, sorted.size() - keep));
        if (older.isEmpty()) {
            return "会话较短，暂无摘要。";
        }
        return older.stream()
                .map(message -> message.role().name() + ": " + abbreviate(message.content()))
                .collect(Collectors.joining(" | "));
    }

    /**
     * 根据缓存消息列表构建简要摘要。
     *
     * @param messages 缓存消息列表
     * @return 拼接摘要，消息为空时返回 null
     */
    public String buildSummaryFromCached(List<AppTutoringCachedMessage> messages) {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.stream()
                .map(message -> message.role() + ": " + abbreviate(message.content()))
                .collect(Collectors.joining(" | "));
    }

    /**
     * 执行abbreviate。
     *
     * @param content 内容
     * @return 执行结果
     */
    private String abbreviate(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        String compact = content.trim().replaceAll("\\s+", " ");
        int max = properties.getSummarySnippetMaxChars();
        return compact.length() <= max ? compact : compact.substring(0, max) + "…";
    }
}
