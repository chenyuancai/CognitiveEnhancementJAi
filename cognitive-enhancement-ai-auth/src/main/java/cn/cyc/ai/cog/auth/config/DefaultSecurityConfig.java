package cn.cyc.ai.cog.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 默认认证安全链：表单登录 + 放行登录/健康检查端点。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Configuration
public class DefaultSecurityConfig {

    /**
     * 执行默认Security过滤器Chain。
     *
     * @param http http
     * @return 执行结果
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/error", "/actuator/**", "/assets/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    /**
     * 执行密码Encoder。
     * @return 执行结果
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // qz_iam_user 存无前缀 BCrypt；OAuth2 客户端密钥使用 {noop} 前缀
        encoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return encoder;
    }
}
