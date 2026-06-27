package cn.cyc.ai.cog.platform.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("qz_kb_content_tag_rel")
public class ContentTagRelEntity {

    private Long contentId;
    private Long tagId;
}
