package cn.cyc.ai.cog.app.tutoring.cache;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.platform.system.support.PlatformRedisCacheSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * C 端 AI 助手会话摘要 Redis 缓存。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringSessionSummaryCache {

    /** 日志记录器。 */
    private static final Logger log = LoggerFactory.getLogger(AppTutoringSessionSummaryCache.class);

    /** Redis 缓存支持组件。 */
    private final PlatformRedisCacheSupport redisCacheSupport;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造会话摘要缓存组件。
     *
     * @param redisCacheSupport Redis 缓存支持组件
     * @param properties        学习辅导配置属性
     */
    public AppTutoringSessionSummaryCache(PlatformRedisCacheSupport redisCacheSupport,
                                          AppTutoringProperties properties) {
        this.redisCacheSupport = redisCacheSupport;
        this.properties = properties;
    }

    /**
     * 安全加载会话摘要，失败时返回空。
     *
     * @param sessionId 会话 ID
     * @return 会话摘要
     */
    public Optional<String> loadSafely(String sessionId) {
        try {
            return redisCacheSupport.get(summaryKey(sessionId), String.class);
        } catch (Exception ex) {
            log.warn("tutoring session summary load failed, sessionId={}", sessionId, ex);
            return Optional.empty();
        }
    }

    /**
     * 安全保存会话摘要，失败时仅记录日志。
     *
     * @param sessionId 会话 ID
     * @param summary   会话摘要
     */
    public void saveSafely(String sessionId, String summary) {
        try {
            redisCacheSupport.put(summaryKey(sessionId), summary, Duration.ofDays(properties.getSummaryTtlDays()));
        } catch (Exception ex) {
            log.warn("tutoring session summary save failed, sessionId={}", sessionId, ex);
        }
    }

    /**
     * 生成会话摘要 Redis 键。
     *
     * @param sessionId 会话 ID
     * @return Redis 键
     */
    static String summaryKey(String sessionId) {
        return "cog:chat:session:" + sessionId + ":summary";
    }
}
