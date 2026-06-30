package cn.cyc.ai.cog.base.api.file;

import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用基础文件 Feign 客户端。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients(clients = BaseFileFeignClient.class)
public @interface EnableBaseFileFeign {
}
