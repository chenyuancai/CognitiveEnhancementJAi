package cn.cyc.ai.cog.platform.billing.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderPageQuery extends PageQuery {

    /** 订单No。 */
    private String orderNo;

    /** buyer用户ID */
    private Long buyerUserId;

    /** 账户ID */
    private Long accountId;

    /** 状态。 */
    private String status;
}
