package cn.cyc.ai.cog.center.prompt.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Prompt 发布指针实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_prompt_release_pointer")
public class PromptReleasePointerEntity extends BaseEntity {

    private Long tenantId;
    private String promptCode;
    private String baselineVersion;
    private String candidateVersion;
    private String grayRuleJson;
}
