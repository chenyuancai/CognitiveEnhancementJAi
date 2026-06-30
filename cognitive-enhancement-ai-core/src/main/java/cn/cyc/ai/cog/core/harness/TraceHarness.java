package cn.cyc.ai.cog.core.harness;

import java.util.Map;

/**
 * 链路治理器，负责执行链路的信息收集与传播。
 * <p>一期的职责：
 * <ul>
 * <li>为每次执行生成/复用 TraceId</li>
 * <li>收集执行路径上的关键节点信息</li>
 * <li>支持自定义标签注入</li>
 * </ul>
 * <p>二期可扩展：分布式链路追踪对接（OpenTelemetry）、链路可视化。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface TraceHarness {

    /**
     * 为新的执行链路初始化 Trace 上下文。
     *
     * @param traceId 外部传入的 TraceId（可为 null，内部自动生成）
     * @param labels  链路标签（如 capabilityCode、agentCode）
     * @return 初始化后的链路上下文
     */
    TraceHarnessContext start(String traceId, Map<String, String> labels);

    /**
     * 记录链路中的一个执行节点。
     *
     * @param context  当前链路上下文
     * @param nodeName 节点名称（如 "AgentRuntime.execute"）
     * @param nodeType 节点类型
     * @param metadata 节点元数据
     */
    void recordNode(TraceHarnessContext context, String nodeName, String nodeType, Map<String, Object> metadata);

    /**
     * 结束链路并生成完整链路报告。
     *
     * @param context 当前链路上下文
     * @return 链路报告
     */
    TraceReport finish(TraceHarnessContext context);

    /**
     * 链路上下文。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    record TraceHarnessContext(String traceId, Map<String, String> labels, long startTimeMs) {
    }

    /**
     * 链路报告。
     *
     * @author cyc
     * @date 2026/6/15 14:18
     */
    record TraceReport(String traceId, long durationMs, Map<String, String> labels, Map<String, Object> path) {
    }
}
