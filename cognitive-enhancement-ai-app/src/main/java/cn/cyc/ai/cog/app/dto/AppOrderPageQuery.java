package cn.cyc.ai.cog.app.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * C 端订单分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppOrderPageQuery extends PageQuery {

    /** 订单状态筛选，可选。 */
    private String status;
}
