package cn.cyc.ai.cog.platform.knowledge.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_kb_content_tag")
public class ContentTagEntity extends BaseEntity {

    private String tagName;
    private String tagColor;
}
