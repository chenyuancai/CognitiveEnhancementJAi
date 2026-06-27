package cn.cyc.ai.cog.platform.operations.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Banner 分页查询参数。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BannerPageQuery extends PageQuery {

    private String keyword;
    private String position;
    private String status;
}
