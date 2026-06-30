package cn.cyc.ai.cog.platform.iam.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户分页查询。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantPageQuery extends PageQuery {

    /** 关键词。 */
    private String keyword;
    /** segment。 */
    private String segment;
    /** 状态。 */
    private String status;
}
