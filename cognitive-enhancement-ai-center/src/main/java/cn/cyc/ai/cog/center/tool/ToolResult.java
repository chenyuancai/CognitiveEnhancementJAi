package cn.cyc.ai.cog.center.tool;

import cn.cyc.ai.cog.core.metadata.tool.ToolProtocolType;
import cn.cyc.ai.cog.core.metadata.type.CommonStatus;
import cn.cyc.ai.cog.core.metadata.type.RiskLevel;
import cn.cyc.ai.cog.core.metadata.type.SchemaDefinition;

/**
 * Tool 定义返回对象。
 *
 * @param toolCode         Tool 编码
 * @param toolName         Tool 名称
 * @param protocolType     协议类型
 * @param requestSchema    请求结构定义
 * @param responseSchema   响应结构定义
 * @param permissionScope  权限范围
 * @param riskLevel        风险等级
 * @param timeoutMs        超时时间
 * @param retryMaxAttempts 最大重试次数
 * @param implRef          实现引用
 * @param status           启用状态
 * @author cyc
 */
public record ToolResult(
        String toolCode,
        String toolName,
        ToolProtocolType protocolType,
        SchemaDefinition requestSchema,
        SchemaDefinition responseSchema,
        String permissionScope,
        RiskLevel riskLevel,
        int timeoutMs,
        int retryMaxAttempts,
        String implRef,
        CommonStatus status
) {
}
