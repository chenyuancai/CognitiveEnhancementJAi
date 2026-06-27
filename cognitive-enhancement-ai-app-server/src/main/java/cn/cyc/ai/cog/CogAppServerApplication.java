package cn.cyc.ai.cog;

import cn.cyc.ai.cog.center.config.CogSeedProperties;
import cn.cyc.ai.cog.runtime.budget.TaskBudgetProperties;
import cn.cyc.ai.cog.runtime.governance.RuntimeQuotaProperties;
import cn.cyc.ai.cog.runtime.model.governance.ModelCircuitBreakerProperties;
import cn.cyc.ai.cog.runtime.planner.PlanningProperties;
import cn.cyc.ai.cog.runtime.policy.PolicyHarnessProperties;
import cn.cyc.ai.cog.runtime.reflection.LoopGuardProperties;
import cn.cyc.ai.cog.runtime.reflection.ReflectionProperties;
import cn.cyc.ai.cog.runtime.security.JwtProperties;
import cn.cyc.ai.cog.runtime.trace.otel.OpenTelemetryTraceProperties;
import cn.cyc.ai.cog.runtime.usage.service.RuntimeUsageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * C 端 App-Server 启动入口（默认端口 8804）。
 * <p>
 * 聚合 app、platform、center、runtime 模块，对外提供 {@code /api/app/**} 路由。
 * </p>
 */
@SpringBootApplication
@EnableConfigurationProperties({
        JwtProperties.class,
        CogSeedProperties.class,
        RuntimeQuotaProperties.class,
        RuntimeUsageProperties.class,
        ModelCircuitBreakerProperties.class,
        OpenTelemetryTraceProperties.class,
        PolicyHarnessProperties.class,
        PlanningProperties.class,
        LoopGuardProperties.class,
        TaskBudgetProperties.class,
        ReflectionProperties.class
})
public class CogAppServerApplication {

    /**
     * 启动 Spring Boot 应用。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(CogAppServerApplication.class, args);
    }
}
