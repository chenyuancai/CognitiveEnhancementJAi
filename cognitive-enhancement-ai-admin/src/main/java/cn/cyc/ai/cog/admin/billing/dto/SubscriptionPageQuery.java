package cn.cyc.ai.cog.admin.billing.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订阅记录分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubscriptionPageQuery extends PageQuery {

    /** 账户ID */
    private Long accountId;
}
