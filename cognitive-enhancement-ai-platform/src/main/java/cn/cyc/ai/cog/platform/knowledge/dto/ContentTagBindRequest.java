package cn.cyc.ai.cog.platform.knowledge.dto;

import lombok.Data;

import java.util.List;

/**
 * 内容标签Bind请求
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class ContentTagBindRequest {

    /** 内容ID */
    private Long contentId;

    /** 标签Ids。 */
    private List<Long> tagIds;
}
