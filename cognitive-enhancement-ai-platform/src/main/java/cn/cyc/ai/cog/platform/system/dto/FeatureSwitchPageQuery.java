package cn.cyc.ai.cog.platform.system.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FeatureSwitchPageQuery extends PageQuery {

    private String keyword;
    private String segment;
}
