package cn.cyc.ai.cog.runtime.session.service;

import cn.cyc.ai.cog.runtime.session.domain.ConversationMessage;

import java.util.List;

/**
 * 本次运行使用的会话上下文快照。
 *
 * @param sessionId       会话 ID
 * @param recentMessages  最近历史消息
 * @param enabled         是否启用上下文
 * @author cyc
 */
public record ConversationContext(
        String sessionId,
        List<ConversationMessage> recentMessages,
        boolean enabled
) {

    public ConversationContext {
        recentMessages = List.copyOf(recentMessages == null ? List.of() : recentMessages);
    }

    public static ConversationContext disabled() {
        return new ConversationContext(null, List.of(), false);
    }
}
