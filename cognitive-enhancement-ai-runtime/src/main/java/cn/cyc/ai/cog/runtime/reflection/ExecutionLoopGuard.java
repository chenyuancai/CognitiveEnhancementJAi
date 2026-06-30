package cn.cyc.ai.cog.runtime.reflection;

import cn.cyc.ai.cog.api.enums.ErrorCode;
import cn.cyc.ai.cog.core.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行循环防护：检测同一 trace 内重复的 Tool/LLM 调用。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class ExecutionLoopGuard {

    /** properties。 */
    private final LoopGuardProperties properties;
    private final ThreadLocal<Map<String, Integer>> counters = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * 创建ExecutionLoopGuard。
     *
     * @param properties properties
     */
    public ExecutionLoopGuard(LoopGuardProperties properties) {
        this.properties = properties;
    }

    /**
     * 记录并校验动作签名，超过阈值时抛出业务异常。
     *
     * @param traceId           链路标识
     * @param actionSignature   动作签名
     */
    public void check(String traceId, String actionSignature) {
        if (!properties.isEnabled()) {
            return;
        }
        String key = traceId + ":" + actionSignature;
        Map<String, Integer> localCounters = counters.get();
        int count = localCounters.merge(key, 1, Integer::sum);
        if (count > properties.getMaxRepeat()) {
            throw new BusinessException(
                    ErrorCode.CONFLICT.getCode(),
                    "检测到重复执行，已触发循环防护: " + actionSignature);
        }
    }

    /**
     * 清理当前线程计数器。
     */
    public void clear() {
        counters.remove();
    }
}
