package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 内容标签Rel实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@TableName("qz_kb_content_tag_rel")
public class ContentTagRelEntity {

    /** 内容ID */
    private Long contentId;
    /** 标签ID */
    private Long tagId;
}
