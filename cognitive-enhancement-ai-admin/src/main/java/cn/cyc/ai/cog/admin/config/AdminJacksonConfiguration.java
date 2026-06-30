package cn.cyc.ai.cog.admin.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 管理后台Jackson配置
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
public class AdminJacksonConfiguration {

    /**
     * 执行longAsStringCustomizer。
     * @return 执行结果
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longAsStringCustomizer() {
        return builder -> builder
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }
}
