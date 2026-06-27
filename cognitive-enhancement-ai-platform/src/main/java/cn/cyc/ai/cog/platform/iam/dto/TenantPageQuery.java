package cn.cyc.ai.cog.platform.iam.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户分页查询。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantPageQuery extends PageQuery {

    private String keyword;
    private String segment;
    private String status;
}
