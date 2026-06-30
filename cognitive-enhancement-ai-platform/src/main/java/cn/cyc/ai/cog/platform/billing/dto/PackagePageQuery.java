package cn.cyc.ai.cog.platform.billing.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Package分页查询条件
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PackagePageQuery extends PageQuery {

    /** segment。 */
    private String segment;
    /** 状态。 */
    private String status;
    /** 关键词。 */
    private String keyword;
}
