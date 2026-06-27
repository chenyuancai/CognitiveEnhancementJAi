package cn.cyc.ai.cog.admin.config;

import cn.cyc.ai.cog.platform.billing.config.PaymentCallbackProperties;
import cn.cyc.ai.cog.admin.security.AuthContextInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({AdminAuthProperties.class, PaymentCallbackProperties.class})
public class AdminWebConfiguration implements WebMvcConfigurer {

    private final AuthContextInterceptor authContextInterceptor;

    public AdminWebConfiguration(AuthContextInterceptor authContextInterceptor) {
        this.authContextInterceptor = authContextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authContextInterceptor)
                .addPathPatterns("/api/admin/**", "/api/runtime/**");
    }
}
