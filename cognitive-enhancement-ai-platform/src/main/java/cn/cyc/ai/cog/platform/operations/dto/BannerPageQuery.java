package cn.cyc.ai.cog.platform.operations.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Banner 分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BannerPageQuery extends PageQuery {

    /** 关键词。 */
    private String keyword;
    /** position。 */
    private String position;
    /** 状态。 */
    private String status;
}
