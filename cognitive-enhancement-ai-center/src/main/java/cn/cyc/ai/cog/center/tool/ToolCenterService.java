package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.common.ListResponse;
import cn.cyc.ai.cog.center.common.SchemaDtoMapper;
import cn.cyc.ai.cog.center.support.AbstractCenterMetadataService;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;

import java.util.List;

/**
 * Tool 管理服务。
 */
public class ToolCenterService extends AbstractCenterMetadataService<ToolDefinition> {

    public ToolCenterService(ToolDefinitionRepository repository) {
        super(repository, "Tool");
    }

    public ListResponse<ToolDtos.Result> list() {
        List<ToolDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    public ToolDtos.Result get(String toolCode) {
        return toResult(getRequired(toolCode));
    }

    public ToolDtos.Result create(ToolDtos.CreateRequest request) {
        ensureAbsent(request.toolCode());
        return toResult(save(new ToolDefinition(
                request.toolCode(),
                request.toolName(),
                request.protocolType(),
                SchemaDtoMapper.toDomain(request.requestSchema()),
                SchemaDtoMapper.toDomain(request.responseSchema()),
                request.permissionScope(),
                defaultRiskLevel(request.riskLevel()),
                request.timeoutMs(),
                new RetryPolicy(request.retryMaxAttempts()),
                request.implRef(),
                request.status()
        )));
    }

    public ToolDtos.Result update(String toolCode, ToolDtos.UpdateRequest request) {
        getRequired(toolCode);
        return toResult(save(new ToolDefinition(
                toolCode,
                request.toolName(),
                request.protocolType(),
                SchemaDtoMapper.toDomain(request.requestSchema()),
                SchemaDtoMapper.toDomain(request.responseSchema()),
                request.permissionScope(),
                defaultRiskLevel(request.riskLevel()),
                request.timeoutMs(),
                new RetryPolicy(request.retryMaxAttempts()),
                request.implRef(),
                request.status()
        )));
    }

    private ToolDtos.Result toResult(ToolDefinition definition) {
        return new ToolDtos.Result(
                definition.toolCode(),
                definition.toolName(),
                definition.protocolType(),
                SchemaDtoMapper.toDto(definition.requestSchema()),
                SchemaDtoMapper.toDto(definition.responseSchema()),
                definition.permissionScope(),
                definition.riskLevel(),
                definition.timeoutMs(),
                definition.retryPolicy().maxAttempts(),
                definition.implRef(),
                definition.status()
        );
    }

    private RiskLevel defaultRiskLevel(RiskLevel riskLevel) {
        return riskLevel == null ? RiskLevel.LOW : riskLevel;
    }
}
