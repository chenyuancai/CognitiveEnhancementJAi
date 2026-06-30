package cn.cyc.ai.cog.runtime.observation.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 能力执行链路摘要实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_execution_record")
public class ExecutionRecordEntity extends BaseEntity {

    /**
     * 链路追踪 ID。
     */
    private String traceId;

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 能力编码。
     */
    private String capabilityCode;

    /**
     * 能力版本。
     */
    private String capabilityVersion;

    /**
     * Agent 编码。
     */
    private String agentCode;

    /**
     * 执行结果状态。
     */
    private String resultStatus;

    /**
     * 是否成功完成执行。
     */
    private Boolean success;

    /**
     * 执行失败原因（成功时为空）。
     */
    private String failureReason;

    /**
     * 执行输入 JSON。
     */
    private String inputJson;

    /**
     * 路由装配 JSON。
     */
    private String routingJson;

    /**
     * 执行结果 JSON。
     */
    private String resultJson;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
