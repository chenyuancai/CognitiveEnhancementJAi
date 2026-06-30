package cn.cyc.ai.cog.center.config;

import cn.cyc.ai.cog.center.user.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web 配置，注册 JWT 认证过滤器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
public class WebConfig {

    /**
     * 执行jwt过滤器Registration。
     *
     * @param filter 过滤器
     * @return 执行结果
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
