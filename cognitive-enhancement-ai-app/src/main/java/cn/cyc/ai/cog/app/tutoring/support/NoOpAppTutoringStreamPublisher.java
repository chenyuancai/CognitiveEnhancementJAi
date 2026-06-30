package cn.cyc.ai.cog.app.tutoring.support;

import cn.cyc.ai.cog.app.tutoring.dto.AppTutoringStreamEvent;

/**
 * SSE 未启用或 Feign 客户端不可用时的空实现推送器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class NoOpAppTutoringStreamPublisher implements AppTutoringStreamPublisher {

    /**
     * {@inheritDoc}
     */
    /**
     * 执行publish。
     *
     * @param userId 用户 ID
     * @param event 事件
     * @param includeUserWideChannel include用户WideChannel
     */
    @Override
    public void publish(Long userId, AppTutoringStreamEvent event, boolean includeUserWideChannel) {
    }
}
