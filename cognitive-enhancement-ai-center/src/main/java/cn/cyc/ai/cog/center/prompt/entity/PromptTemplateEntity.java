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
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_ai_prompt_template")
public class PromptTemplateEntity extends BaseEntity {

    /** 租户 ID */
    private Long tenantId;
    /** 提示词编码。 */
    private String promptCode;
    /** 提示词名称。 */
    private String promptName;
    /** scenario编码。 */
    private String scenarioCode;
    /** 版本号 */
    private String version;
    /** template内容。 */
    private String templateContent;
    /** variableSchema。 */
    private String variableSchema;
    /** 输出Schema。 */
    private String outputSchema;
    /** 状态。 */
    private String status;
    /** lifecycle状态。 */
    private String lifecycleStatus;
    /** grayRuleJSON。 */
    private String grayRuleJson;
    /** publishedAt。 */
    private LocalDateTime publishedAt;
}
