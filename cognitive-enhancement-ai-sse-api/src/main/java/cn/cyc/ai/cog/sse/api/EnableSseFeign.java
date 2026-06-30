package cn.cyc.ai.cog.sse.api;

import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 SSE Feign 客户端。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients(clients = SseFeignClient.class)
public @interface EnableSseFeign {
}
