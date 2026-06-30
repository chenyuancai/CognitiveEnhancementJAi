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
 * @date 2026/6/15 14:18
 */
public class AgentCenterService extends AbstractCenterMetadataService<AgentDefinition> {

    /**
     * 创建智能体Center服务。
     *
     * @param repository 仓储
     */
    public AgentCenterService(AgentDefinitionRepository repository) {
        super(repository, "Agent");
    }

    /**
     * 查询Item列表。
     * @return 结果列表
     */
    public ListResponse<AgentDtos.Result> list() {
        List<AgentDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    /**
     * 执行get。
     *
     * @param agentCode 智能体编码
     * @return 执行结果
     */
    public AgentDtos.Result get(String agentCode) {
        return toResult(getRequired(agentCode));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
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

    /**
     * 更新Item。
     *
     * @param agentCode 智能体编码
     * @param request 请求
     * @return 更新结果
     */
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

    /**
     * 转换为结果。
     *
     * @param definition definition
     * @return 转换结果
     */
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
