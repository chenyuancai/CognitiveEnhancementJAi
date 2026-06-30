package cn.cyc.ai.cog.app.tutoring.context;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringCachedMessage;
import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringHotHistoryCache;
import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * C 端 AI 助手上下文加载器：Redis 优先，DB 回源。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringContextLoader {

    /** 热历史 Redis 缓存。 */
    private final AppTutoringHotHistoryCache hotHistoryCache;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造上下文加载器。
     *
     * @param hotHistoryCache 热历史缓存
     * @param properties      学习辅导配置属性
     */
    public AppTutoringContextLoader(AppTutoringHotHistoryCache hotHistoryCache,
                                    AppTutoringProperties properties) {
        this.hotHistoryCache = hotHistoryCache;
        this.properties = properties;
    }

    /**
     * 加载会话上下文，组装 Prompt 所需的历史消息与引用信息。
     *
     * @param sessionId        会话 ID
     * @param dbMessages       数据库消息列表
     * @param sessionSummary   会话摘要
     * @param resolvedContext  解析后的引用上下文
     * @return 已加载上下文
     */
    public AppTutoringLoadedContext load(String sessionId,
                                         List<ConversationMessage> dbMessages,
                                         String sessionSummary,
                                         AppTutoringResolvedContext resolvedContext) {
        List<AppTutoringCachedMessage> redisMessages = hotHistoryCache.loadSafely(sessionId);
        boolean loadedFromRedis = !redisMessages.isEmpty();
        List<AppTutoringCachedMessage> recentMessages = loadedFromRedis
                ? redisMessages
                : toCachedMessages(dbMessages, properties.getHotHistoryMaxMessages());
        boolean loadedFromDb = !loadedFromRedis && !recentMessages.isEmpty();
        List<AppTutoringCachedMessage> promptMessages = tail(recentMessages, properties.getContextMaxMessages()).stream()
                .map(this::truncateForPrompt)
                .toList();
        return new AppTutoringLoadedContext(
                recentMessages,
                promptMessages,
                sessionSummary,
                resolvedContext,
                loadedFromRedis,
                loadedFromDb);
    }

    /**
     * 将数据库消息转换为缓存消息格式。
     *
     * @param messages    数据库消息列表
     * @param maxMessages 最大保留条数
     * @return 缓存消息列表
     */
    private List<AppTutoringCachedMessage> toCachedMessages(List<ConversationMessage> messages, int maxMessages) {
        return messages.stream()
                .sorted(Comparator.comparing(ConversationMessage::recordedAt))
                .skip(Math.max(0, messages.size() - maxMessages))
                .map(message -> new AppTutoringCachedMessage(
                        message.messageId(),
                        message.role().name(),
                        message.content(),
                        message.traceId(),
                        message.recordedAt()))
                .toList();
    }

    /**
     * 截取消息列表尾部指定条数。
     *
     * @param messages    消息列表
     * @param maxMessages 最大条数
     * @return 尾部消息列表
     */
    private List<AppTutoringCachedMessage> tail(List<AppTutoringCachedMessage> messages, int maxMessages) {
        if (messages.size() <= maxMessages) {
            return new ArrayList<>(messages);
        }
        return new ArrayList<>(messages.subList(messages.size() - maxMessages, messages.size()));
    }

    /**
     * 按配置截断单条消息内容以适配 Prompt 长度限制。
     *
     * @param message 原始缓存消息
     * @return 截断后的缓存消息
     */
    private AppTutoringCachedMessage truncateForPrompt(AppTutoringCachedMessage message) {
        int maxChars = properties.getContextMaxCharsPerMessage();
        String content = message.content();
        if (content == null || content.length() <= maxChars) {
            return message;
        }
        return new AppTutoringCachedMessage(
                message.messageId(),
                message.role(),
                content.substring(0, maxChars) + "…",
                message.traceId(),
                message.recordedAt());
    }
}
