package cn.cyc.ai.cog.runtime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ReAct Agent 执行配置。
 */
@ConfigurationProperties(prefix = "cog.runtime.react")
public class ReActProperties {

    /**
     * 绑定 Tool 时是否默认走 ReAct 多轮循环（可通过请求参数 reactEnabled=false 关闭）。
     */
    private boolean enabled = true;

    /**
     * 最大 ReAct 迭代次数。
     */
    private int maxIterations = 5;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
