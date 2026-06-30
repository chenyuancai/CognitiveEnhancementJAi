package cn.cyc.ai.cog.runtime.knowledge.entity;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 知识片段实体。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_rt_knowledge_fragment")
public class KnowledgeFragmentEntity extends BaseEntity {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 片段 ID。
     */
    private String fragmentId;

    /**
     * 知识库编码。
     */
    private String knowledgeCode;

    /**
     * 标题。
     */
    private String title;

    /**
     * 内容。
     */
    private String content;

    /**
     * 标签 JSON。
     */
    private String tagsJson;

    /**
     * 状态。
     */
    private String status;

    /**
     * 记录时间。
     */
    private Instant recordedAt;
}
