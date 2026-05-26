package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;

import java.util.List;

/**
 * Agent 管理服务。
 *
 * @author cyc
 */
public class AgentCenterService extends AbstractCenterMetadataService<AgentDefinition> {

    public AgentCenterService(AgentDefinitionRepository repository) {
        super(repository, "Agent");
    }

    public ListResponse<AgentDtos.Result> list() {
        List<AgentDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    public AgentDtos.Result get(String agentCode) {
        return toResult(getRequired(agentCode));
    }

    public AgentDtos.Result create(AgentDtos.CreateRequest request) {
        ensureAbsent(request.agentCode());
        return toResult(save(new AgentDefinition(
                request.agentCode(),
                request.agentName(),
                request.roleDesc(),
                request.goalDesc(),
                request.modelCode(),
                request.maxSteps(),
                request.maxCost(),
                request.timeoutMs(),
                request.allowedSkillCodes(),
                request.parameterConstraints(),
                request.status()
        )));
    }

    public AgentDtos.Result update(String agentCode, AgentDtos.UpdateRequest request) {
        getRequired(agentCode);
        return toResult(save(new AgentDefinition(
                agentCode,
                request.agentName(),
                request.roleDesc(),
                request.goalDesc(),
                request.modelCode(),
                request.maxSteps(),
                request.maxCost(),
                request.timeoutMs(),
                request.allowedSkillCodes(),
                request.parameterConstraints(),
                request.status()
        )));
    }

    private AgentDtos.Result toResult(AgentDefinition definition) {
        return new AgentDtos.Result(
                definition.agentCode(),
                definition.agentName(),
                definition.roleDesc(),
                definition.goalDesc(),
                definition.modelCode(),
                definition.maxSteps(),
                definition.maxCost(),
                definition.timeoutMs(),
                definition.allowedSkillCodes(),
                definition.parameterConstraints(),
                definition.status()
        );
    }
}
