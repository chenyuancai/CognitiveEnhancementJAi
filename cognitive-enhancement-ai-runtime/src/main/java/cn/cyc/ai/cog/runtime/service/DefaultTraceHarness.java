package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.core.harness.TraceHarness;
import cn.cyc.ai.cog.core.trace.TraceContext;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TraceHarness 默认实现，委托现有 TraceContext/TraceIdGenerator。
 *
 * @author cyc
 */
@Component
public class DefaultTraceHarness implements TraceHarness {

    /**
     * TraceId 生成器。
     */
    private final TraceIdGenerator traceIdGenerator;

    /**
     * 构造默认链路治理器。
     *
     * @param traceIdGenerator TraceId 生成器
     */
    public DefaultTraceHarness(TraceIdGenerator traceIdGenerator) {
        this.traceIdGenerator = traceIdGenerator;
    }

    /**
     * 初始化链路上下文。
     *
     * @param traceId 外部传入的 TraceId
     * @param labels  链路标签
     * @return 链路上下文
     */
    @Override
    public TraceHarnessContext start(String traceId, Map<String, String> labels) {
        String actualTraceId = traceId != null ? traceId : traceIdGenerator.generate();
        TraceContext.setTraceId(actualTraceId);
        return new TraceHarnessContext(actualTraceId, labels, System.currentTimeMillis());
    }

    /**
     * 记录执行节点，收集节点信息。
     *
     * @param context  链路上下文
     * @param nodeName 节点名称
     * @param nodeType 节点类型
     * @param metadata 节点元数据
     */
    @Override
    public void recordNode(TraceHarnessContext context, String nodeName, String nodeType, Map<String, Object> metadata) {
        // 当前为内存收集，可扩展为写入链路存储
    }

    /**
     * 结束链路并生成报告。
     *
     * @param context 链路上下文
     * @return 链路报告
     */
    @Override
    public TraceReport finish(TraceHarnessContext context) {
        long durationMs = System.currentTimeMillis() - context.startTimeMs();
        return new TraceReport(context.traceId(), durationMs, context.labels(), Map.of());
    }
}
