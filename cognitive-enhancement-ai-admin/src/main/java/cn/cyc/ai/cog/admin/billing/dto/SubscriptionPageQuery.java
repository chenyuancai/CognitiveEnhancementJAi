package cn.cyc.ai.cog.admin.billing.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订阅记录分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubscriptionPageQuery extends PageQuery {

    private Long accountId;
}
