package cn.cyc.ai.cog.center.capability.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Capability 发布指针实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_capability_release_pointer")
public class CapabilityReleasePointerEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 能力编码。 */
    private String capabilityCode;
    /** baseline版本号。 */
    private String baselineVersion;
    /** candidate版本号。 */
    private String candidateVersion;
    /** grayRuleJSON。 */
    private String grayRuleJson;
}
