package cn.cyc.ai.cog.runtime.config;

import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.step.AgentExecuteStep;
import cn.cyc.ai.cog.runtime.harness.step.BeanValidationStep;
import cn.cyc.ai.cog.runtime.harness.step.CapabilityRouteStep;
import cn.cyc.ai.cog.runtime.harness.step.LlmInvokeStep;
import cn.cyc.ai.cog.runtime.harness.step.RepositoryHealthStep;
import cn.cyc.ai.cog.runtime.harness.step.ScenarioAssemblyStep;
import cn.cyc.ai.cog.runtime.harness.step.SkillLoadStep;
import cn.cyc.ai.cog.runtime.harness.step.ToolInvokeStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Harness 测试框架配置。
 *
 * @author cyc
 */
@Configuration
public class HarnessConfiguration {

    @Bean
    public List<HarnessStep> harnessSteps(
            BeanValidationStep beanStep,
            RepositoryHealthStep repoStep,
            ScenarioAssemblyStep assemblyStep,
            CapabilityRouteStep capStep,
            AgentExecuteStep agentStep,
            SkillLoadStep skillStep,
            ToolInvokeStep toolStep,
            LlmInvokeStep llmStep) {
        return List.of(beanStep, repoStep, assemblyStep, capStep, agentStep, skillStep, toolStep, llmStep);
    }
}
