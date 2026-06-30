package cn.cyc.ai.cog.app.tutoring.support;

/**
 * C 端 AI 学习辅导常量定义。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public final class AppTutoringConstants {

    /** 默认会话能力编码。 */
    public static final String SESSION_CAPABILITY_CODE = "capability.chat.tutoring";

    /** 会员权益编码：AI 带学。 */
    public static final String BENEFIT_CODE = "ai.tutoring";

    /** 默认会员分段标识（2C）。 */
    public static final String DEFAULT_SEGMENT = "2C";

    /** Redis 会话消息键前缀。 */
    public static final String REDIS_MESSAGES_KEY_PREFIX = "cog:chat:session:";

    /**
     * 创建C端辅导常量定义。
     */
    private AppTutoringConstants() {
    }
}
