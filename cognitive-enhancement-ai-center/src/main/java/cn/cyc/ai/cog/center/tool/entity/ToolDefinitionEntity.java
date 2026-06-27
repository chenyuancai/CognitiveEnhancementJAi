package cn.cyc.ai.cog.center.tool.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工具定义数据库实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_tool_definition")
public class ToolDefinitionEntity extends BaseEntity {

    private Long tenantId;
    private String toolCode;
    private String toolName;
    private String protocolType;
    private String requestSchema;
    private String responseSchema;
    private String permissionScope;
    private String riskLevel;
    private Integer timeoutMs;
    private Integer retryMaxAttempts;
    private String implRef;
    private String status;
}
