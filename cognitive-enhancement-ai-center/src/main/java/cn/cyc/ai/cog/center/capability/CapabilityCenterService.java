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
 */
public class CapabilityCenterService extends AbstractCenterMetadataService<CapabilityDefinition> {

    public CapabilityCenterService(CapabilityDefinitionRepository repository) {
        super(repository, "能力");
    }

    public ListResponse<CapabilityDtos.Result> list() {
        List<CapabilityDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    public CapabilityDtos.Result get(String capabilityCode) {
        return toResult(getRequired(capabilityCode));
    }

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
