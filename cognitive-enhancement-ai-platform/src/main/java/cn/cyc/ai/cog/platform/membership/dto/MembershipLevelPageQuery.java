package cn.cyc.ai.cog.platform.membership.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员等级分页查询条件
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MembershipLevelPageQuery extends PageQuery {

    /** segment。 */
    private String segment;
    /** 状态。 */
    private String status;
}
