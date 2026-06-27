package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容发布版本快照（只追加，映射 qz_kb_content_version）。
 */
@Data
@TableName("qz_kb_content_version")
public class ContentVersionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long contentId;

    private Integer versionNo;

    private String title;

    private String summary;

    private String body;

    @TableField("min_level_code")
    private String minLevelCode;

    private Long operatorId;

    private LocalDateTime createTime;
}
