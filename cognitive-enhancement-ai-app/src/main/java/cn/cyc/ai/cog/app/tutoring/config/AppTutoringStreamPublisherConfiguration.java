package cn.cyc.ai.cog.app.tutoring.config;

import cn.cyc.ai.cog.app.tutoring.support.AppTutoringStreamPublisher;
import cn.cyc.ai.cog.app.tutoring.support.NoOpAppTutoringStreamPublisher;
import cn.cyc.ai.cog.app.tutoring.support.SseFeignAppTutoringStreamPublisher;
import cn.cyc.ai.cog.sse.api.SseFeignClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 学习辅导 SSE 推送器装配：优先 Feign 实现，否则 NoOp 兜底。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
public class AppTutoringStreamPublisherConfiguration {

    /**
     * 基于 Feign 的 SSE 流式事件推送器。
     *
     * @param sseFeignClient SSE Feign 客户端
     * @return 流式事件推送器
     */
    /**
     * 执行sseFeignC端辅导流Publisher。
     *
     * @param sseFeignClient sseFeign客户端
     * @return 执行结果
     */
    @Bean
    @ConditionalOnProperty(name = "cog.sse.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnBean(SseFeignClient.class)
    public AppTutoringStreamPublisher sseFeignAppTutoringStreamPublisher(SseFeignClient sseFeignClient) {
        return new SseFeignAppTutoringStreamPublisher(sseFeignClient);
    }

    /**
     * SSE 不可用时的空实现推送器。
     *
     * @return 空实现推送器
     */
    /**
     * 执行noOpC端辅导流Publisher。
     * @return 执行结果
     */
    @Bean
    @ConditionalOnMissingBean(AppTutoringStreamPublisher.class)
    public AppTutoringStreamPublisher noOpAppTutoringStreamPublisher() {
        return new NoOpAppTutoringStreamPublisher();
    }
}
