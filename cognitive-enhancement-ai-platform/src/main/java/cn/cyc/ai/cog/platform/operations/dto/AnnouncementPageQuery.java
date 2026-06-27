package cn.cyc.ai.cog.platform.operations.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnnouncementPageQuery extends PageQuery {

    private String keyword;
    private String status;
}
