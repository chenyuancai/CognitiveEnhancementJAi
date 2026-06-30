package cn.cyc.ai.cog.platform.knowledge.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 内容ImportJob分页查询条件
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentImportJobPageQuery extends PageQuery {

    /** 状态。 */
    private String status;
}
