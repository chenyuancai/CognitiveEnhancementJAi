package cn.cyc.ai.cog.app.tutoring.cache;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis 热历史消息包装，用于序列化会话最近消息列表。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class AppTutoringCachedMessages {

    /** 最近消息列表。 */
    private List<AppTutoringCachedMessage> messages = new ArrayList<>();
}
