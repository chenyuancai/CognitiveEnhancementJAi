package cn.cyc.ai.cog.platform.knowledge.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容标签实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_kb_content_tag")
public class ContentTagEntity extends BaseEntity {

    /** 标签名称。 */
    private String tagName;
    /** 标签Color。 */
    private String tagColor;
}
