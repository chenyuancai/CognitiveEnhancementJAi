package cn.cyc.ai.cog.sse.support;

import cn.cyc.ai.cog.sse.config.SseProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 本机 SSE 连接表（单副本内存持有，业务服务通过 Feign 推送）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Slf4j
@Component
public class SseConnectionManager {

    /**
     * properties。
     */
    private final SseProperties properties;
    /**
     * JSON 序列化器
     */
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, SseConnection>> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> heartbeatTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(2, runnable -> {
        Thread thread = new Thread(runnable, "sse-heartbeat");
        thread.setDaemon(true);
        return thread;
    });
    /**
     * connection数量。
     */
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    /**
     * connectionIDGenerator。
     */
    private final AtomicLong connectionIdGenerator = new AtomicLong(0);

    /**
     * 创建SseConnectionManager。
     *
     * @param properties   properties
     * @param objectMapper JSON 序列化器
     */
    public SseConnectionManager(SseProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    /**
     * 执行connect。
     *
     * @param receiverKey receiver键
     * @return 执行结果
     */
    public SseEmitter connect(String receiverKey) {
        String connectionId = "conn_" + connectionIdGenerator.incrementAndGet();
        SseEmitter emitter = new SseEmitter(properties.getConnectionTimeoutMs());
        SseConnection connection = new SseConnection(connectionId, receiverKey, emitter);
        cache.computeIfAbsent(receiverKey, ignored -> new ConcurrentHashMap<>()).put(connectionId, connection);
        connectionCount.incrementAndGet();
        registerCallbacks(receiverKey, connection);
        startHeartbeat(connection);
        log.info("SSE 连接建立: receiverKey={}, connectionId={}, total={}", receiverKey, connectionId, connectionCount.get());
        try {
            connection.sendMessage("connection", Map.of(
                    "status", "connected",
                    "receiverKey", receiverKey,
                    "connectionId", connectionId
            ));
        } catch (Exception exception) {
            log.warn("发送连接成功事件失败: receiverKey={}, connectionId={}", receiverKey, connectionId, exception);
        }
        return emitter;
    }

    /**
     * 执行disconnect。
     *
     * @param receiverKey receiver键
     */
    public void disconnect(String receiverKey) {
        ConcurrentHashMap<String, SseConnection> connections = cache.remove(receiverKey);
        if (connections == null || connections.isEmpty()) {
            return;
        }
        int closed = 0;
        for (SseConnection connection : connections.values()) {
            stopHeartbeat(connection.getConnectionId());
            if (connection.closeConnectionSafely()) {
                closed++;
            }
        }
        if (closed > 0) {
            connectionCount.addAndGet(-closed);
        }
        log.info("SSE 连接断开: receiverKey={}, closed={}", receiverKey, closed);
    }

    /**
     * 执行send。
     *
     * @param receiverKey receiver键
     * @param eventName   事件名称
     * @param payload     payload
     * @return 执行结果
     */
    public boolean send(String receiverKey, String eventName, Object payload) {
        ConcurrentHashMap<String, SseConnection> connections = cache.get(receiverKey);
        if (connections == null || connections.isEmpty()) {
            log.warn("无活跃 SSE 连接: receiverKey={}", receiverKey);
            return false;
        }
        String data = serialize(payload);
        List<String> failed = new ArrayList<>();
        boolean delivered = false;
        for (Map.Entry<String, SseConnection> entry : connections.entrySet()) {
            SseConnection connection = entry.getValue();
            if (!connection.isActive()) {
                failed.add(entry.getKey());
                continue;
            }
            try {
                connection.sendMessage(eventName, data);
                delivered = true;
            } catch (Exception exception) {
                log.warn("SSE 推送失败: receiverKey={}, connectionId={}, event={}",
                        receiverKey, entry.getKey(), eventName, exception);
                failed.add(entry.getKey());
            }
        }
        failed.forEach(connectionId -> removeConnection(receiverKey, connectionId));
        return delivered;
    }

    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConnections", connectionCount.get());
        stats.put("receiverCount", cache.size());
        stats.put("heartbeatTasks", heartbeatTasks.size());
        return stats;
    }

    /**
     * 执行shutdown。
     */
    @PreDestroy
    public void shutdown() {
        heartbeatTasks.values().forEach(task -> task.cancel(false));
        heartbeatTasks.clear();
        heartbeatExecutor.shutdownNow();
        cache.keySet().forEach(this::disconnect);
        log.info("SSE 连接管理器已关闭");
    }

    /**
     * 执行registerCallbacks。
     *
     * @param receiverKey receiver键
     * @param connection  connection
     */
    private void registerCallbacks(String receiverKey, SseConnection connection) {
        String connectionId = connection.getConnectionId();
        connection.getEmitter().onCompletion(() -> removeConnection(receiverKey, connectionId));
        connection.getEmitter().onTimeout(() -> removeConnection(receiverKey, connectionId));
        connection.getEmitter().onError(throwable -> removeConnection(receiverKey, connectionId));
    }

    /**
     * 执行startHeartbeat。
     *
     * @param connection connection
     */
    private void startHeartbeat(SseConnection connection) {
        String connectionId = connection.getConnectionId();
        long interval = properties.getHeartbeatIntervalMs();
        ScheduledFuture<?> task = heartbeatExecutor.scheduleWithFixedDelay(() -> {
            if (!connection.isActive()) {
                stopHeartbeat(connectionId);
                return;
            }
            try {
                connection.sendMessage("heartbeat", Map.of(
                        "timestamp", System.currentTimeMillis(),
                        "connectionId", connectionId
                ));
            } catch (Exception exception) {
                removeConnection(connection.getReceiverKey(), connectionId);
            }
        }, interval, interval, TimeUnit.MILLISECONDS);
        heartbeatTasks.put(connectionId, task);
    }

    /**
     * 执行stopHeartbeat。
     *
     * @param connectionId connectionID
     */
    private void stopHeartbeat(String connectionId) {
        ScheduledFuture<?> task = heartbeatTasks.remove(connectionId);
        if (task != null) {
            task.cancel(false);
        }
    }

    /**
     * 删除Connection。
     *
     * @param receiverKey  receiver键
     * @param connectionId connectionID
     */
    private void removeConnection(String receiverKey, String connectionId) {
        ConcurrentHashMap<String, SseConnection> connections = cache.get(receiverKey);
        if (connections == null) {
            return;
        }
        SseConnection connection = connections.remove(connectionId);
        if (connection == null) {
            return;
        }
        stopHeartbeat(connectionId);
        if (connection.closeConnectionSafely()) {
            connectionCount.decrementAndGet();
        }
        if (connections.isEmpty()) {
            cache.remove(receiverKey);
        }
        log.debug("SSE 连接移除: receiverKey={}, connectionId={}, total={}", receiverKey, connectionId, connectionCount.get());
    }

    /**
     * 执行serialize。
     *
     * @param payload payload
     * @return 执行结果
     */
    private String serialize(Object payload) {
        if (payload == null) {
            return "{}";
        }
        if (payload instanceof String text) {
            return text;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("SSE 载荷序列化失败", exception);
        }
    }
}
