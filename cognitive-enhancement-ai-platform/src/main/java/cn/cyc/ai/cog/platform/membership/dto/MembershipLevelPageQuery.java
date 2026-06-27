package cn.cyc.ai.cog.platform.membership.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MembershipLevelPageQuery extends PageQuery {

    private String segment;
    private String status;
}
