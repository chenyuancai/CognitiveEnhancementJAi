package cn.cyc.ai.cog.center.capability.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 能力定义数据库实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_capability_definition")
public class CapabilityDefinitionEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 能力编码。 */
    private String capabilityCode;
    /** 能力名称。 */
    private String capabilityName;
    /** 能力Desc。 */
    private String capabilityDesc;
    /** 版本号 */
    private String version;
    /** 输入Schema。 */
    private String inputSchema;
    /** 输出Schema。 */
    private String outputSchema;
    /** parameterConstraints。 */
    private String parameterConstraints;
    /** execute模式。 */
    private String executeMode;
    /** bound智能体编码。 */
    private String boundAgentCode;
    /** risk等级。 */
    private String riskLevel;
    /** needHumanConfirm。 */
    private Integer needHumanConfirm;
    /** 状态。 */
    private String status;
    /** lifecycle状态。 */
    private String lifecycleStatus;
    /** publishedAt。 */
    private java.time.LocalDateTime publishedAt;
}
