package cn.cyc.ai.cog.platform.iam.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询条件
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends PageQuery {

    /** 关键词。 */
    private String keyword;
    /** 状态。 */
    private String status;
    /** 用户类型。 */
    private String userType;
    /** 等级编码。 */
    private String levelCode;
}
