package cn.cyc.ai.cog.admin.quota.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Token 流水分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TokenRecordPageQuery extends PageQuery {

    private Long accountId;
}
