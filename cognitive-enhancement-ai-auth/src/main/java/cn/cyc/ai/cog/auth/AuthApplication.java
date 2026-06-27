package cn.cyc.ai.cog.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证服务启动类（OAuth2 授权服务，独立进程）。
 *
 * @author cyc
 */
@SpringBootApplication
@MapperScan("cn.cyc.ai.cog.auth")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
