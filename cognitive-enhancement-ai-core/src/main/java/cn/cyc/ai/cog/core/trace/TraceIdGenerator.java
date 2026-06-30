package cn.cyc.ai.cog.core.trace;

/**
 * TraceId 生成器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@FunctionalInterface
public interface TraceIdGenerator {

    String generate();
}
