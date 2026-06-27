package cn.cyc.ai.cog.center.capability.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 能力定义数据库实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_capability_definition")
public class CapabilityDefinitionEntity extends BaseEntity {

    private Long tenantId;
    private String capabilityCode;
    private String capabilityName;
    private String capabilityDesc;
    private String version;
    private String inputSchema;
    private String outputSchema;
    private String parameterConstraints;
    private String executeMode;
    private String boundAgentCode;
    private String riskLevel;
    private Integer needHumanConfirm;
    private String status;
    private String lifecycleStatus;
    private java.time.LocalDateTime publishedAt;
}
