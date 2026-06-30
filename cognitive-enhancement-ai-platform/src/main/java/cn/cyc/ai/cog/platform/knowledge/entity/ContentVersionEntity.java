package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容发布版本快照（只追加，映射 qz_kb_content_version）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_kb_content_version")
public class ContentVersionEntity {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户 ID */
    private Long tenantId;

    /** 内容ID */
    private Long contentId;

    /** 版本号，每次更新递增 */
    private Integer versionNo;

    /** 标题。 */
    private String title;

    /** 摘要。 */
    private String summary;

    /** body。 */
    private String body;

    /** min等级编码。 */
    @TableField("min_level_code")
    private String minLevelCode;

    /** operatorID */
    private Long operatorId;

    /** 创建时间 */
    private LocalDateTime createTime;
}
