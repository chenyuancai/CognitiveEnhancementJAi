package cn.cyc.ai.cog.admin.membership.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员变更审计分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MembershipChangeLogPageQuery extends PageQuery {

    private Long accountId;
}
