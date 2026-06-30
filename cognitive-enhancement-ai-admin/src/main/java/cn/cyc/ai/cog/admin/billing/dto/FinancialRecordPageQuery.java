package cn.cyc.ai.cog.admin.billing.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资金流水分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FinancialRecordPageQuery extends PageQuery {

    /** 账户ID */
    private Long accountId;

    /** 订单ID */
    private Long orderId;
}
