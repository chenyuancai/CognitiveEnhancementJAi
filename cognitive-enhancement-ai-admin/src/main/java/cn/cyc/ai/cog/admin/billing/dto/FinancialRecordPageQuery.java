package cn.cyc.ai.cog.admin.billing.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资金流水分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FinancialRecordPageQuery extends PageQuery {

    private Long accountId;

    private Long orderId;
}
