package cn.cyc.ai.cog.runtime.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类。
 *
 * @author cyc
 */
@Configuration
@MapperScan({
        "cn.cyc.ai.cog.runtime.**.mapper",
        "cn.cyc.ai.cog.center.**.mapper",
        "cn.cyc.ai.cog.center.user",
        "cn.cyc.ai.cog.admin.**.mapper",
        "cn.cyc.ai.cog.platform.**.mapper"
})
public class MybatisPlusConfig {
}
