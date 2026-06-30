package cn.cyc.ai.cog.center.capability;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.common.SchemaDtoMapper;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinition;
import cn.cyc.ai.cog.core.metadata.capability.CapabilityDefinitionRepository;

import java.util.List;

/**
 * Capability 管理服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class CapabilityCenterService extends AbstractCenterMetadataService<CapabilityDefinition> {

    /**
     * 创建能力Center服务。
     *
     * @param repository 仓储
     */
    public CapabilityCenterService(CapabilityDefinitionRepository repository) {
        super(repository, "能力");
    }

    /**
     * 查询Item列表。
     * @return 结果列表
     */
    public ListResponse<CapabilityDtos.Result> list() {
        List<CapabilityDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    /**
     * 执行get。
     *
     * @param capabilityCode 能力编码
     * @return 执行结果
     */
    public CapabilityDtos.Result get(String capabilityCode) {
        return toResult(getRequired(capabilityCode));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    public CapabilityDtos.Result create(CapabilityDtos.CreateRequest request) {
        ensureAbsent(request.capabilityCode());
        return toResult(save(new CapabilityDefinition(
                request.capabilityCode(),
                request.capabilityName(),
                request.capabilityDesc(),
                SchemaDtoMapper.toDomain(request.inputSchema()),
                SchemaDtoMapper.toDomain(request.outputSchema()),
                request.parameterConstraints(),
                request.executeMode(),
                request.boundAgentCode(),
                request.riskLevel(),
                request.needHumanConfirm(),
                request.status()
        )));
    }

    /**
     * 更新Item。
     *
     * @param capabilityCode 能力编码
     * @param request 请求
     * @return 更新结果
     */
    public CapabilityDtos.Result update(String capabilityCode, CapabilityDtos.UpdateRequest request) {
        getRequired(capabilityCode);
        return toResult(save(new CapabilityDefinition(
                capabilityCode,
                request.capabilityName(),
                request.capabilityDesc(),
                SchemaDtoMapper.toDomain(request.inputSchema()),
                SchemaDtoMapper.toDomain(request.outputSchema()),
                request.parameterConstraints(),
                request.executeMode(),
                request.boundAgentCode(),
                request.riskLevel(),
                request.needHumanConfirm(),
                request.status()
        )));
    }

    /**
     * 转换为结果。
     *
     * @param definition definition
     * @return 转换结果
     */
    private CapabilityDtos.Result toResult(CapabilityDefinition definition) {
        return new CapabilityDtos.Result(
                definition.capabilityCode(),
                definition.capabilityName(),
                definition.capabilityDesc(),
                SchemaDtoMapper.toDto(definition.inputSchema()),
                SchemaDtoMapper.toDto(definition.outputSchema()),
                definition.parameterConstraints(),
                definition.executeMode(),
                definition.boundAgentCode(),
                definition.riskLevel(),
                definition.needHumanConfirm(),
                definition.status()
        );
    }
}
