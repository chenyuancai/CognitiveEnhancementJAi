package cn.cyc.ai.cog.center.capability.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Capability 发布指针实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_capability_release_pointer")
public class CapabilityReleasePointerEntity extends BaseEntity {

    private Long tenantId;
    private String capabilityCode;
    private String baselineVersion;
    private String candidateVersion;
    private String grayRuleJson;
}
