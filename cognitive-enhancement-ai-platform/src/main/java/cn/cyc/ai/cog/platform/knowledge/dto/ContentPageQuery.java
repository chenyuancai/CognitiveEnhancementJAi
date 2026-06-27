package cn.cyc.ai.cog.platform.knowledge.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容分页查询参数。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentPageQuery extends PageQuery {

    /** 关键字：标题模糊。 */
    private String keyword;

    /** 内容类型。 */
    private String type;

    /** 状态。 */
    private String status;
}
