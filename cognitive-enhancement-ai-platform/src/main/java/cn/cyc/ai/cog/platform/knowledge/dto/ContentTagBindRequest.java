package cn.cyc.ai.cog.platform.knowledge.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContentTagBindRequest {

    private Long contentId;

    private List<Long> tagIds;
}
