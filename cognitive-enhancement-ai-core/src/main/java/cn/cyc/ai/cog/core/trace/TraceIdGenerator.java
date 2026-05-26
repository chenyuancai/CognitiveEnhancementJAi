package cn.cyc.ai.cog.core.trace;

/**
 * TraceId 生成器。
 */
@FunctionalInterface
public interface TraceIdGenerator {

    String generate();
}
