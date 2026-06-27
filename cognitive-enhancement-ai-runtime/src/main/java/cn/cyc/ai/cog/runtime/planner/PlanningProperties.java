package cn.cyc.ai.cog.runtime.planner;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 任务规划配置。
 *
 * @author cyc
 */
@Data
@ConfigurationProperties(prefix = "cog.runtime.planner")
public class PlanningProperties {

    /**
     * 是否启用任务规划。
     */
    private boolean enabled = true;

    /**
     * 是否启用 LLM 驱动规划。
     */
    private boolean llmEnabled = true;
}
