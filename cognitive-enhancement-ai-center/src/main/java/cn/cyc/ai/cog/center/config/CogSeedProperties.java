package cn.cyc.ai.cog.center.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 运行时种子数据开关。
 *
 * <p>与 {@code cog.persistence.enabled} 组合决定种子来源：
 * <ul>
 *   <li>持久化开启：Flyway 迁移（V2~V6）负责表结构与演示元数据；{@link AdminUserInitializer} 负责 admin 账号</li>
 *   <li>持久化关闭：{@link CenterDemoDataInitializer} 负责内存演示元数据</li>
 * </ul>
 *
 * @author cyc
 */
@ConfigurationProperties(prefix = "cog.seed")
public class CogSeedProperties {

    /**
     * 是否启用运行时种子（admin / demo 元数据）。
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
