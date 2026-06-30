package cn.cyc.ai.cog.runtime.session.service;

import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;

import java.util.List;

/**
 * 本次运行使用的会话上下文快照。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record ConversationContext(
        String sessionId,
        List<ConversationMessage> recentMessages,
        boolean enabled
) {

    public ConversationContext {
        recentMessages = List.copyOf(recentMessages == null ? List.of() : recentMessages);
    }

    /**
     * 执行disabled。
     * @return 执行结果
     */
    public static ConversationContext disabled() {
        return new ConversationContext(null, List.of(), false);
    }
}
