package cn.cyc.ai.cog.center.agent;

import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinition;
import cn.cyc.ai.cog.core.metadata.agent.AgentDefinitionRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Agent 管理服务。
 *
 * @author cyc
 */
@Service
public class AgentAdminService extends AbstractMetadataAdminService<AgentDefinition, AgentUpsertRequest, AgentResult> {

    /**
     * 创建 Agent 后台管理服务。
     *
     * @param repository Agent 定义仓储
     */
    public AgentAdminService(AgentDefinitionRepository repository) {
        super(repository);
    }

    /**
     * 将 Agent 写入请求转换为 Agent 定义。
     *
     * @param request      Agent 写入请求
     * @param overrideCode 覆盖编码
     * @return Agent 定义
     */
    @Override
    protected AgentDefinition toDefinition(AgentUpsertRequest request, String overrideCode) {
        String agentCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.agentCode(), "agentCode 不能为空");
        return new AgentDefinition(
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
        );
    }

    /**
     * 将 Agent 定义转换为返回对象。
     *
     * @param definition Agent 定义
     * @return Agent 返回对象
     */
    @Override
    protected AgentResult toResult(AgentDefinition definition) {
        return new AgentResult(
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
