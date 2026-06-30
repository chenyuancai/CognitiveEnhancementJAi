package cn.cyc.ai.cog.runtime.session.dto;

import cn.cyc.ai.cog.runtime.session.domain.MessageRole;

/**
 * 追加会话消息请求。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public record AppendMessageRequest(
        MessageRole role,
        String content,
        String traceId
) {
}
