package cn.cyc.ai.cog.admin.config;

import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.admin.security.AuthContextInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 管理后台Web配置
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableConfigurationProperties({AdminAuthProperties.class, PaymentCallbackProperties.class})
public class AdminWebConfiguration implements WebMvcConfigurer {

    /** 认证上下文拦截器。 */
    private final AuthContextInterceptor authContextInterceptor;

    /**
     * 创建管理后台Web配置。
     *
     * @param authContextInterceptor 认证上下文拦截器
     */
    public AdminWebConfiguration(AuthContextInterceptor authContextInterceptor) {
        this.authContextInterceptor = authContextInterceptor;
    }

    /**
     * 执行addInterceptors。
     *
     * @param registry registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authContextInterceptor)
                .addPathPatterns("/api/admin/**", "/api/runtime/**");
    }
}
