package cn.cyc.ai.cog.infra.web;

import cn.cyc.ai.cog.core.trace.DefaultTraceIdGenerator;
import cn.cyc.ai.cog.core.trace.TraceIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web 基础设施装配。
 *
 * @author cyc
 */
@Configuration
public class TraceWebConfiguration {

    /**
     * 提供默认 TraceId 生成器。
     *
     * @return TraceId 生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public TraceIdGenerator traceIdGenerator() {
        return new DefaultTraceIdGenerator();
    }

    /**
     * 装配 Trace 上下文过滤器。
     *
     * @param traceIdGenerator TraceId 生成器
     * @return Trace 过滤器
     */
    @Bean
    public TraceContextFilter traceContextFilter(TraceIdGenerator traceIdGenerator) {
        return new TraceContextFilter(traceIdGenerator);
    }
}
