package cn.cyc.ai.cog.runtime.session.dto;

import cn.cyc.ai.cog.runtime.session.domain.MessageRole;

/**
 * 追加会话消息请求。
 *
 * @param role    消息角色
 * @param content 消息内容
 * @param traceId 关联 TraceId
 * @author cyc
 */
public record AppendMessageRequest(
        MessageRole role,
        String content,
        String traceId
) {
}
