package cn.cyc.ai.cog.runtime.reflection;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 执行循环防护配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.loop-guard")
public class LoopGuardProperties {

    /**
     * 是否启用循环检测。
     */
    private boolean enabled = true;

    /**
     * 同一 trace 内允许的最大重复次数。
     */
    private int maxRepeat = 3;
}
