package cn.cyc.ai.cog.app.tutoring.cache;

import cn.cyc.ai.cog.app.tutoring.config.AppTutoringProperties;
import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStudentState;
import cn.cyc.ai.cog.platform.system.support.PlatformRedisCacheSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * C 端 AI 助手学习状态 Redis 缓存。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class AppTutoringLearningStateCache {

    /** 日志记录器。 */
    private static final Logger log = LoggerFactory.getLogger(AppTutoringLearningStateCache.class);

    /** Redis 缓存支持组件。 */
    private final PlatformRedisCacheSupport redisCacheSupport;

    /** 学习辅导配置属性。 */
    private final AppTutoringProperties properties;

    /**
     * 构造学习状态缓存组件。
     *
     * @param redisCacheSupport Redis 缓存支持组件
     * @param properties        学习辅导配置属性
     */
    public AppTutoringLearningStateCache(PlatformRedisCacheSupport redisCacheSupport,
                                         AppTutoringProperties properties) {
        this.redisCacheSupport = redisCacheSupport;
        this.properties = properties;
    }

    /**
     * 安全加载会话学习状态，失败时返回空。
     *
     * @param sessionId 会话 ID
     * @return 学生学习状态
     */
    public Optional<AppTutoringStudentState> loadSafely(String sessionId) {
        try {
            return redisCacheSupport.get(stateKey(sessionId), AppTutoringStudentState.class);
        } catch (Exception ex) {
            log.warn("tutoring learning state load failed, sessionId={}", sessionId, ex);
            return Optional.empty();
        }
    }

    /**
     * 安全保存会话学习状态，失败时仅记录日志。
     *
     * @param sessionId 会话 ID
     * @param state     学生学习状态
     */
    public void saveSafely(String sessionId, AppTutoringStudentState state) {
        try {
            redisCacheSupport.put(stateKey(sessionId), state, Duration.ofDays(properties.getLearningStateTtlDays()));
        } catch (Exception ex) {
            log.warn("tutoring learning state save failed, sessionId={}", sessionId, ex);
        }
    }

    /**
     * 生成学习状态 Redis 键。
     *
     * @param sessionId 会话 ID
     * @return Redis 键
     */
    static String stateKey(String sessionId) {
        return "cog:learning:session:" + sessionId + ":state";
    }
}
