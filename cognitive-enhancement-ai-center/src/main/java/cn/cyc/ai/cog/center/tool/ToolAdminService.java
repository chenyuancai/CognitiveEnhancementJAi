package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.common.CenterPageResult;
import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Tool 管理服务。
 *
 * @author cyc
 */
@Service
public class ToolAdminService extends AbstractMetadataAdminService<ToolDefinition, ToolUpsertRequest, ToolResult> {

    /**
     * 创建 Tool 后台管理服务。
     *
     * @param repository Tool 定义仓储
     */
    public ToolAdminService(ToolDefinitionRepository repository) {
        super(repository);
    }

    /**
     * 分页查询 Tool 定义。
     *
     * @param query 查询参数
     * @return 分页 Tool 列表
     */
    public CenterPageResult<ToolResult> listPage(ToolPageQuery query) {
        return listPage(
                query,
                definition -> matches(query.getProtocolType(), definition.protocolType())
                        && matches(query.getRiskLevel(), definition.riskLevel()),
                ToolDefinition::status,
                toolSorters()
        );
    }

    /**
     * 将 Tool 写入请求转换为 Tool 定义。
     *
     * @param request      Tool 写入请求
     * @param overrideCode 覆盖编码
     * @return Tool 定义
     */
    @Override
    protected ToolDefinition toDefinition(ToolUpsertRequest request, String overrideCode) {
        String toolCode = overrideCode != null ? overrideCode : Objects.requireNonNull(request.toolCode(), "toolCode 不能为空");
        return new ToolDefinition(
                toolCode,
                request.toolName(),
                request.protocolType(),
                request.requestSchema(),
                request.responseSchema(),
                request.permissionScope(),
                defaultRiskLevel(request.riskLevel()),
                request.timeoutMs(),
                new RetryPolicy(request.retryMaxAttempts()),
                request.implRef(),
                request.status()
        );
    }

    /**
     * 将 Tool 定义转换为返回对象。
     *
     * @param definition Tool 定义
     * @return Tool 返回对象
     */
    @Override
    protected ToolResult toResult(ToolDefinition definition) {
        return new ToolResult(
                definition.toolCode(),
                definition.toolName(),
                definition.protocolType(),
                definition.requestSchema(),
                definition.responseSchema(),
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

    private Map<String, Comparator<ToolDefinition>> toolSorters() {
        Map<String, Comparator<ToolDefinition>> sorters = new LinkedHashMap<>(commonSorters(ToolDefinition::status));
        sorters.put("protocolType", Comparator.comparing(definition -> definition.protocolType().name()));
        sorters.put("riskLevel", Comparator.comparing(definition -> definition.riskLevel().name()));
        sorters.put("timeoutMs", Comparator.comparingInt(ToolDefinition::timeoutMs));
        return sorters;
    }

    private boolean matches(ToolProtocolType expected, ToolProtocolType actual) {
        return expected == null || expected == actual;
    }

    private boolean matches(RiskLevel expected, RiskLevel actual) {
        return expected == null || expected == actual;
    }
}
