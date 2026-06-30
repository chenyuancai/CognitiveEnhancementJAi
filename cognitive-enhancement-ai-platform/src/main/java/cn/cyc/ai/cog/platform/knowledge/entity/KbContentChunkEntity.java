package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识内容分块实体。
 */
@Data
@TableName("qz_kb_content_chunk")
public class KbContentChunkEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("content_id")
    private Long contentId;

    @TableField("task_code")
    private String taskCode;

    @TableField("chunk_index")
    private Integer chunkIndex;

    @TableField("heading_path")
    private String headingPath;

    @TableField("chunk_text")
    private String chunkText;

    @TableField("token_est")
    private Integer tokenEst;

    @TableField("create_time")
    private LocalDateTime createTime;
}
