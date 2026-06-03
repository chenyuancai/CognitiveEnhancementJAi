package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.runtime.spi.AgentRuntime;
import cn.cyc.ai.cog.runtime.spi.CapabilityRuntime;
import cn.cyc.ai.cog.runtime.spi.LlmGateway;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean 装配检查步骤，验证核心 Spring Bean 已正确注册。
 *
 * @author cyc
 */
@Component
public class BeanValidationStep implements HarnessStep {

    private final ApplicationContext applicationContext;

    private static final List<Class<?>> REQUIRED_BEANS = List.of(
            CapabilityDefinitionRepository.class,
            AgentDefinitionRepository.class,
            SkillDefinitionRepository.class,
            ToolDefinitionRepository.class,
            ModelDefinitionRepository.class,
            CapabilityRuntime.class,
            AgentRuntime.class,
            LlmGateway.class
    );

    public BeanValidationStep(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String stepCode() {
        return "BEAN_VALIDATION";
    }

    @Override
    public String stepName() {
        return "组件装配检查";
    }

    @Override
    public String description() {
        return "验证核心 Spring Bean 已正确注册";
    }

    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        List<Map<String, Object>> checkedBeans = new ArrayList<>();
        List<String> missingBeans = new ArrayList<>();

        for (Class<?> beanType : REQUIRED_BEANS) {
            Map<String, Object> beanInfo = new LinkedHashMap<>();
            beanInfo.put("name", beanType.getSimpleName());
            try {
                Object bean = applicationContext.getBean(beanType);
                beanInfo.put("type", bean.getClass().getSimpleName());
                beanInfo.put("found", true);
                checkedBeans.add(beanInfo);
            } catch (Exception ex) {
                beanInfo.put("found", false);
                checkedBeans.add(beanInfo);
                missingBeans.add(beanType.getSimpleName());
            }
        }

        boolean passed = missingBeans.isEmpty();
        String message = passed
                ? checkedBeans.size() + " 个核心 Bean 全部正常注册"
                : "缺失 Bean: " + String.join(", ", missingBeans);

        return new HarnessStepResult(
                stepCode(), stepName(), passed, 0, message,
                Map.of("checkedBeans", checkedBeans, "missingBeans", missingBeans)
        );
    }
}
