package cn.cyc.ai.cog.runtime.observation.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 能力调用用量记录实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_usage_record")
public class UsageRecordEntity extends BaseEntity {

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
     * 执行器类型。
     */
    private String executorType;

    /**
     * 模型编码。
     */
    private String modelCode;

    /**
     * Tool 编码。
     */
    private String toolCode;

    /**
     * 输入 token 数。
     */
    private Integer inputTokenCount;

    /**
     * 输出 token 数。
     */
    private Integer outputTokenCount;

    /**
     * 总 token 数。
     */
    private Integer totalTokenCount;

    /**
     * 预估成本。
     */
    private BigDecimal estimatedCostAmount;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
