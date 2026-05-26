package cn.cyc.ai.cog.core.trace;

import java.util.UUID;

/**
 * 默认 TraceId 生成器。
 */
public class DefaultTraceIdGenerator implements TraceIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
