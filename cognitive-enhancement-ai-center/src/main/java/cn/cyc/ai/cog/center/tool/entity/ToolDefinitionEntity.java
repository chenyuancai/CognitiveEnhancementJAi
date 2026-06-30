package cn.cyc.ai.cog.center.tool.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工具定义数据库实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_tool_definition")
public class ToolDefinitionEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 工具编码。 */
    private String toolCode;
    /** 工具名称。 */
    private String toolName;
    /** protocol类型。 */
    private String protocolType;
    /** 请求Schema。 */
    private String requestSchema;
    /** 响应Schema。 */
    private String responseSchema;
    /** 权限Scope。 */
    private String permissionScope;
    /** risk等级。 */
    private String riskLevel;
    /** timeoutMs。 */
    private Integer timeoutMs;
    /** retryMaxAttempts。 */
    private Integer retryMaxAttempts;
    /** implRef。 */
    private String implRef;
    /** 状态。 */
    private String status;
}
