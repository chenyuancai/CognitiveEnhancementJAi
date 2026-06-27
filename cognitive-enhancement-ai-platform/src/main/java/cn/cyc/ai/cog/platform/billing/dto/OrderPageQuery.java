package cn.cyc.ai.cog.platform.billing.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderPageQuery extends PageQuery {

    private String orderNo;

    private Long buyerUserId;

    private Long accountId;

    private String status;
}
