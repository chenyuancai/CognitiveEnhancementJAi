package cn.cyc.ai.cog.platform.knowledge.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容实体（映射 qz_kb_content）。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_kb_content")
public class ContentEntity extends BaseEntity {

    /** 标题。 */
    private String title;

    /** 内容类型：ARTICLE/NOTICE/FAQ 等。 */
    @TableField("content_type")
    private String contentType;

    /** 作者。 */
    private String author;

    /** 状态：DRAFT/PENDING/PUBLISHED/REJECTED/OFFLINE。 */
    private String status;

    /** 摘要。 */
    private String summary;

    /** 正文。 */
    private String body;

    /** 审核备注。 */
    private String auditRemark;

    /** 可读最低会员等级（null/FREE=全员可读）。 */
    @com.baomidou.mybatisplus.annotation.TableField("min_level_code")
    private String minLevelCode;

    /** 当前已发布版本号。 */
    @TableField("current_version")
    private Integer currentVersion;

    /** 最近发布时间。 */
    @TableField("published_at")
    private java.time.LocalDateTime publishedAt;
}
