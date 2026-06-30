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
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public class ToolCenterService extends AbstractCenterMetadataService<ToolDefinition> {

    /**
     * 创建工具Center服务。
     *
     * @param repository 仓储
     */
    public ToolCenterService(ToolDefinitionRepository repository) {
        super(repository, "Tool");
    }

    /**
     * 查询Item列表。
     * @return 结果列表
     */
    public ListResponse<ToolDtos.Result> list() {
        List<ToolDtos.Result> items = repository().listAll().stream().map(this::toResult).toList();
        return new ListResponse<>(items, items.size());
    }

    /**
     * 执行get。
     *
     * @param toolCode 工具编码
     * @return 执行结果
     */
    public ToolDtos.Result get(String toolCode) {
        return toResult(getRequired(toolCode));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
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

    /**
     * 更新Item。
     *
     * @param toolCode 工具编码
     * @param request 请求
     * @return 更新结果
     */
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

    /**
     * 转换为结果。
     *
     * @param definition definition
     * @return 转换结果
     */
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

    /**
     * 执行默认Risk等级。
     *
     * @param riskLevel risk等级
     * @return 执行结果
     */
    private RiskLevel defaultRiskLevel(RiskLevel riskLevel) {
        return riskLevel == null ? RiskLevel.LOW : riskLevel;
    }
}
