package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.center.support.AbstractMetadataAdminService;
import cn.cyc.ai.cog.core.metadata.tool.RetryPolicy;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinition;
import cn.cyc.ai.cog.core.metadata.tool.ToolDefinitionRepository;
import org.springframework.stereotype.Service;

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
                definition.timeoutMs(),
                definition.retryPolicy().maxAttempts(),
                definition.implRef(),
                definition.status()
        );
    }
}
