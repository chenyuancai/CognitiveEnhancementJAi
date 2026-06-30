package cn.cyc.ai.cog.app.tutoring.context;

import cn.cyc.ai.cog.app.tutoring.cache.AppTutoringCachedMessage;

import java.util.List;

/**
 * C 端 AI 助手已加载上下文，包含热历史、Prompt 消息与引用信息。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AppTutoringLoadedContext(
        List<AppTutoringCachedMessage> recentMessages,
        List<AppTutoringCachedMessage> promptMessages,
        String sessionSummary,
        AppTutoringResolvedContext resolvedContext,
        boolean loadedFromRedis,
        boolean loadedFromDb) {
}
