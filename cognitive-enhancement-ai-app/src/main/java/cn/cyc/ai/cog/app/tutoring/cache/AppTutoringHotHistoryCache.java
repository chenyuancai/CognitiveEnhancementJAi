package cn.cyc.ai.cog.app.tutoring.cache;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.platform.system.support.PlatformRedisCacheSupport;
import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

/**
 * C 端学习辅导 Redis 热历史缓存，缓存会话最近消息以加速上下文加载。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringHotHistoryCache {

    /** 日志记录器。 */
    private static final Logger log = LoggerFactory.getLogger(AppTutoringHotHistoryCache.class);

    /** Redis 缓存支持组件。 */
    private final PlatformRedisCacheSupport redisCacheSupport;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造热历史缓存组件。
     *
     * @param redisCacheSupport Redis 缓存支持组件
     * @param properties        学习辅导配置属性
     */
    public AppTutoringHotHistoryCache(PlatformRedisCacheSupport redisCacheSupport,
                                      AppTutoringProperties properties) {
        this.redisCacheSupport = redisCacheSupport;
        this.properties = properties;
    }

    /**
     * 从 Redis 加载会话热历史消息。
     *
     * @param sessionId 会话 ID
     * @return 缓存消息列表
     */
    public List<AppTutoringCachedMessage> load(String sessionId) {
        return redisCacheSupport.get(messagesKey(sessionId), AppTutoringCachedMessages.class)
                .map(AppTutoringCachedMessages::getMessages)
                .orElse(List.of());
    }

    /**
     * 安全加载会话热历史消息，失败时返回空列表。
     *
     * @param sessionId 会话 ID
     * @return 缓存消息列表
     */
    public List<AppTutoringCachedMessage> loadSafely(String sessionId) {
        try {
            return load(sessionId);
        } catch (Exception ex) {
            log.warn("tutoring hot history load failed, sessionId={}", sessionId, ex);
            return List.of();
        }
    }

    /**
     * 根据数据库消息刷新 Redis 热历史缓存。
     *
     * @param sessionId 会话 ID
     * @param messages  会话消息列表
     */
    public void refresh(String sessionId, List<ConversationMessage> messages) {
        List<AppTutoringCachedMessage> cached = messages.stream()
                .sorted(Comparator.comparing(ConversationMessage::recordedAt))
                .skip(Math.max(0, messages.size() - properties.getHotHistoryMaxMessages()))
                .map(message -> new AppTutoringCachedMessage(
                        message.messageId(),
                        message.role().name(),
                        message.content(),
                        message.traceId(),
                        message.recordedAt()))
                .toList();
        AppTutoringCachedMessages wrapper = new AppTutoringCachedMessages();
        wrapper.setMessages(cached);
        redisCacheSupport.put(messagesKey(sessionId), wrapper, Duration.ofDays(properties.getHotHistoryTtlDays()));
    }

    /**
     * 安全刷新 Redis 热历史缓存，失败时仅记录日志。
     *
     * @param sessionId 会话 ID
     * @param messages  会话消息列表
     */
    public void refreshSafely(String sessionId, List<ConversationMessage> messages) {
        try {
            refresh(sessionId, messages);
        } catch (Exception ex) {
            log.warn("tutoring hot history refresh failed, sessionId={}", sessionId, ex);
        }
    }

    /**
     * 生成热历史消息 Redis 键。
     *
     * @param sessionId 会话 ID
     * @return Redis 键
     */
    static String messagesKey(String sessionId) {
        return "cog:chat:session:" + sessionId + ":messages";
    }
}
