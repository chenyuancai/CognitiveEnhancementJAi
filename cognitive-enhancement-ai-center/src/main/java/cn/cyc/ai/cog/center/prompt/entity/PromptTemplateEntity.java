package cn.cyc.ai.cog.center.prompt.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 提示词模板数据库实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_prompt_template")
public class PromptTemplateEntity extends BaseEntity {

    private Long tenantId;
    private String promptCode;
    private String promptName;
    private String scenarioCode;
    private String version;
    private String templateContent;
    private String variableSchema;
    private String outputSchema;
    private String status;
    private String lifecycleStatus;
    private String grayRuleJson;
    private LocalDateTime publishedAt;
}
