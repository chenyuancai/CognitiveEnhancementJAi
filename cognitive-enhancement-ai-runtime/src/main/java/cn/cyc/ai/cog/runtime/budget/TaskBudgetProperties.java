package cn.cyc.ai.cog.runtime.budget;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 任务预算配置。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.budget")
public class TaskBudgetProperties {

    /**
     * 是否启用任务级预算控制。
     */
    private boolean enabled = true;
}
