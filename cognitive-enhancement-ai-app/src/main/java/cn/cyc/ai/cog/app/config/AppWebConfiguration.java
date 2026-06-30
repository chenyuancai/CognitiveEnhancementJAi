package cn.cyc.ai.cog.app.config;

import cn.cyc.ai.cog.app.security.AppAuthContextInterceptor;
import cn.cyc.ai.cog.app.security.AppRateLimitInterceptor;
import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * C 端 Web MVC 配置：注册鉴权拦截器与配置属性。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
@EnableConfigurationProperties({AppAuthProperties.class, AppRateLimitProperties.class, AppReadCacheProperties.class, PaymentCallbackProperties.class})
public class AppWebConfiguration implements WebMvcConfigurer {

    /** C 端鉴权上下文拦截器 */
    private final AppAuthContextInterceptor appAuthContextInterceptor;

    /** C 端限流拦截器 */
    private final AppRateLimitInterceptor appRateLimitInterceptor;

    /**
     * @param appAuthContextInterceptor 鉴权拦截器
     * @param appRateLimitInterceptor   限流拦截器
     */
    public AppWebConfiguration(AppAuthContextInterceptor appAuthContextInterceptor,
                               AppRateLimitInterceptor appRateLimitInterceptor) {
        this.appAuthContextInterceptor = appAuthContextInterceptor;
        this.appRateLimitInterceptor = appRateLimitInterceptor;
    }

    /**
     * 为 {@code /api/app/**} 注册鉴权拦截器。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appRateLimitInterceptor).addPathPatterns("/api/app/**");
        registry.addInterceptor(appAuthContextInterceptor).addPathPatterns("/api/app/**");
    }
}
