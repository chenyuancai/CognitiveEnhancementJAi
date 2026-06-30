package cn.cyc.ai.cog.runtime.config;

import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.step.AgentExecuteStep;
import cn.cyc.ai.cog.runtime.harness.step.BeanValidationStep;
import cn.cyc.ai.cog.runtime.harness.step.CapabilityRouteStep;
import cn.cyc.ai.cog.runtime.harness.step.ImportKbFileParseHarnessStep;
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
 * @date 2026/6/15 14:18
 */
@Configuration
public class HarnessConfiguration {

    /**
     * 执行harnessSteps。
     * @return 执行结果
     */
    @Bean
    public List<HarnessStep> harnessSteps(
            BeanValidationStep beanStep,
            RepositoryHealthStep repoStep,
            ScenarioAssemblyStep assemblyStep,
            CapabilityRouteStep capStep,
            AgentExecuteStep agentStep,
            SkillLoadStep skillStep,
            ToolInvokeStep toolStep,
            LlmInvokeStep llmStep,
            ImportKbFileParseHarnessStep importKbStep) {
        return List.of(beanStep, repoStep, assemblyStep, capStep, agentStep, skillStep, toolStep, llmStep, importKbStep);
    }
}
