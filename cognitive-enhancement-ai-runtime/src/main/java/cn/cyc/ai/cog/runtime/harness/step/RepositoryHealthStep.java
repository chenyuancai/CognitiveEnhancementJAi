package cn.cyc.ai.cog.runtime.harness.step;

import cn.cyc.ai.cog.core.metadata.MetadataRepository;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.model.ModelDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.skill.SkillDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessContext;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessScenario;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStep;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessStepResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓储健康检查步骤，验证各仓储可读写且场景编码存在。
 *
 * @author cyc
 */
@Component
public class RepositoryHealthStep implements HarnessStep {

    private final CapabilityDefinitionRepository capabilityRepository;
    private final AgentDefinitionRepository agentRepository;
    private final SkillDefinitionRepository skillRepository;
    private final ToolDefinitionRepository toolRepository;
    private final ModelDefinitionRepository modelRepository;

    public RepositoryHealthStep(CapabilityDefinitionRepository capabilityRepository,
                                 AgentDefinitionRepository agentRepository,
                                 SkillDefinitionRepository skillRepository,
                                 ToolDefinitionRepository toolRepository,
                                 ModelDefinitionRepository modelRepository) {
        this.capabilityRepository = capabilityRepository;
        this.agentRepository = agentRepository;
        this.skillRepository = skillRepository;
        this.toolRepository = toolRepository;
        this.modelRepository = modelRepository;
    }

    @Override
    public String stepCode() {
        return "REPO_HEALTH";
    }

    @Override
    public String stepName() {
        return "仓储健康检查";
    }

    @Override
    public String description() {
        return "验证各仓储可读写且场景编码存在";
    }

    @Override
    public HarnessStepResult run(HarnessContext ctx) {
        List<Map<String, Object>> repoResults = new ArrayList<>();
        boolean allHealthy = true;

        allHealthy = checkRepository(repoResults, "CapabilityDefinitionRepository", capabilityRepository) && allHealthy;
        allHealthy = checkRepository(repoResults, "AgentDefinitionRepository", agentRepository) && allHealthy;
        allHealthy = checkRepository(repoResults, "SkillDefinitionRepository", skillRepository) && allHealthy;
        allHealthy = checkRepository(repoResults, "ToolDefinitionRepository", toolRepository) && allHealthy;
        allHealthy = checkRepository(repoResults, "ModelDefinitionRepository", modelRepository) && allHealthy;

        Map<String, Object> scenarioExists = checkScenarioExists(ctx);

        boolean passed = allHealthy && (boolean) scenarioExists.get("allFound");
        String message = passed
                ? "5 个仓储读写正常，场景编码全部存在"
                : "仓储或场景检查未通过";

        return new HarnessStepResult(
                stepCode(), stepName(), passed, 0, message,
                Map.of("repositories", repoResults, "scenarioExists", scenarioExists)
        );
    }

    private boolean checkRepository(List<Map<String, Object>> results, String repoName,
                                     MetadataRepository<?> repository) {
        long start = System.currentTimeMillis();
        boolean healthy;
        try {
            repository.listAll();
            healthy = true;
        } catch (Exception ex) {
            healthy = false;
        }
        long latencyMs = System.currentTimeMillis() - start;
        results.add(Map.of("type", repoName, "healthy", healthy, "latencyMs", latencyMs));
        return healthy;
    }

    private Map<String, Object> checkScenarioExists(HarnessContext ctx) {
        Map<String, Object> result = new LinkedHashMap<>();
        boolean allFound = true;

        if (ctx.scenario() == null) {
            result.put("allFound", false);
            result.put("error", "场景配置为空");
            return result;
        }

        HarnessScenario scenario = ctx.scenario();

        boolean agentFound = scenario.agentCode() != null && agentRepository.findByCode(scenario.agentCode()).isPresent();
        result.put("agentCode", Map.of("found", agentFound, "code", scenario.agentCode()));
        allFound = allFound && agentFound;

        if (scenario.capabilityCode() != null) {
            boolean capFound = capabilityRepository.findByCode(scenario.capabilityCode()).isPresent();
            result.put("capabilityCode", Map.of("found", capFound, "code", scenario.capabilityCode()));
            allFound = allFound && capFound;
        }

        if (scenario.modelCode() != null) {
            boolean modelFound = modelRepository.findByCode(scenario.modelCode()).isPresent();
            result.put("modelCode", Map.of("found", modelFound, "code", scenario.modelCode()));
            allFound = allFound && modelFound;
        }

        result.put("allFound", allFound);
        return result;
    }
}
