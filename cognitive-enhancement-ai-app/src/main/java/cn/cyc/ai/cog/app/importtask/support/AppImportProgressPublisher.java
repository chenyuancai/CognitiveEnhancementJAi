package cn.cyc.ai.cog.app.importtask.support;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 导入进度 SSE 订阅器（单进程内存，按 taskCode 广播）。
 *
 * @author cyc
 * @date 2026/6/29
 */
@Component
public class AppImportProgressPublisher {

    /** taskCode → 订阅回调列表 */
    private final Map<String, List<Consumer<Map<String, Object>>>> listeners = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * 订阅指定任务的进度事件。
     */
    public void subscribe(String taskCode, Consumer<Map<String, Object>> listener) {
        listeners.computeIfAbsent(taskCode, key -> new CopyOnWriteArrayList<>()).add(listener);
    }

    /**
     * 向已订阅客户端推送事件。
     */
    public void publish(String taskCode, Map<String, Object> event) {
        listeners.getOrDefault(taskCode, List.of()).forEach(listener -> listener.accept(event));
    }

    /**
     * 移除任务全部订阅（连接断开时可选调用）。
     */
    public void unsubscribe(String taskCode) {
        listeners.remove(taskCode);
    }
}
