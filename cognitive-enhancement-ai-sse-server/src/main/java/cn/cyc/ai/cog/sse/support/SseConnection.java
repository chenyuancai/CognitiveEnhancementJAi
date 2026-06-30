package cn.cyc.ai.cog.sse.support;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.atomic.AtomicReference;

/**
 * SSE 连接包装，提供状态管理与安全关闭。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Slf4j
@Getter
public class SseConnection {

    /**
     * ConnectionState 枚举
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    public enum ConnectionState {
        /** active。 */
        ACTIVE,
        /** closing。 */
        CLOSING,
        CLOSED
    }

    /** connectionID */
    private final String connectionId;
    /** receiver键。 */
    private final String receiverKey;
    /** emitter。 */
    private final SseEmitter emitter;
    /** 状态。 */
    private final AtomicReference<ConnectionState> state = new AtomicReference<>(ConnectionState.ACTIVE);
    /** 创建时间 */
    private final long createTime = System.currentTimeMillis();
    private volatile long lastActiveTime = createTime;

    public SseConnection(String connectionId, String receiverKey, SseEmitter emitter) {
        this.connectionId = connectionId;
        this.receiverKey = receiverKey;
        this.emitter = emitter;
    }

    /**
     * 判断是否为Active。
     * @return 是否满足条件
     */
    public boolean isActive() {
        return state.get() == ConnectionState.ACTIVE;
    }

    /**
     * 更新LastActive时间。
     * @return 更新结果
     */
    public void updateLastActiveTime() {
        lastActiveTime = System.currentTimeMillis();
    }

    /**
     * 执行closeConnectionSafely。
     * @return 执行结果
     */
    public boolean closeConnectionSafely() {
        if (state.compareAndSet(ConnectionState.ACTIVE, ConnectionState.CLOSING)) {
            try {
                emitter.complete();
            } catch (Exception exception) {
                log.debug("关闭 SSE 连接异常: connectionId={}, message={}", connectionId, exception.getMessage());
            } finally {
                state.set(ConnectionState.CLOSED);
            }
            return true;
        }
        return false;
    }

    /**
     * 执行send消息。
     *
     * @param eventName 事件名称
     * @param data 数据
     */
    public void sendMessage(String eventName, Object data) throws Exception {
        if (!isActive()) {
            throw new IllegalStateException("连接不可用: " + connectionId);
        }
        emitter.send(SseEmitter.event().name(eventName).data(data));
        updateLastActiveTime();
    }
}
