package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识向量索引记录。
 */
@Data
@TableName("qz_kb_vector_index_record")
public class KbVectorIndexRecordEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("content_id")
    private Long contentId;

    @TableField("chunk_id")
    private Long chunkId;

    @TableField("model_code")
    private String modelCode;

    private Integer dim;

    @TableField("vector_json")
    private String vectorJson;

    @TableField("create_time")
    private LocalDateTime createTime;
}
