package cn.cyc.ai.cog.center.prompt.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Prompt 发布指针实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_prompt_release_pointer")
public class PromptReleasePointerEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 提示词编码。 */
    private String promptCode;
    /** baseline版本号。 */
    private String baselineVersion;
    /** candidate版本号。 */
    private String candidateVersion;
    /** grayRuleJSON。 */
    private String grayRuleJson;
}
