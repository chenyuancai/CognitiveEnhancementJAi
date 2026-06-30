package cn.cyc.ai.cog.platform.tutoring.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会话摘要实体（映射 qz_app_conversation_summary）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_app_conversation_summary")
public class ConversationSummaryEntity extends BaseEntity {

    /** 租户 ID。 */
    /** 租户 ID */
    @TableField("tenant_id")
    private Long tenantId;

    /** 会话 ID。 */
    /** 会话 ID */
    @TableField("session_id")
    private String sessionId;

    /** 摘要文本内容。 */
    /** 摘要Text。 */
    @TableField("summary_text")
    private String summaryText;

    /** 版本号，每次更新递增。 */
    /** 版本号，每次更新递增 */
    @TableField("version_no")
    private Integer versionNo;
}
