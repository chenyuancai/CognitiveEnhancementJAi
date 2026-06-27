package cn.cyc.ai.cog.platform.knowledge.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContentImportJobPageQuery extends PageQuery {

    private String status;
}
