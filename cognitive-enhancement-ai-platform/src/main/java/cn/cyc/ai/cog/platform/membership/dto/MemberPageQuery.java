package cn.cyc.ai.cog.platform.membership.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPageQuery extends PageQuery {

    private Long accountId;

    private String levelCode;
}
