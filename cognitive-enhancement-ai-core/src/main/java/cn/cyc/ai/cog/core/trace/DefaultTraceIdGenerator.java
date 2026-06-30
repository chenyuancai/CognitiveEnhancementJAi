package cn.cyc.ai.cog.core.trace;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 默认 TraceId 生成器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class DefaultTraceIdGenerator implements TraceIdGenerator {

    /**
     * 执行generate。
     * @return 执行结果
     */
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
