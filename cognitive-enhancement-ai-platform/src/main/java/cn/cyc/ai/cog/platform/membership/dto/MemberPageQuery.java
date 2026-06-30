package cn.cyc.ai.cog.platform.membership.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPageQuery extends PageQuery {

    /** 账户ID */
    private Long accountId;

    /** 等级编码。 */
    private String levelCode;
}
