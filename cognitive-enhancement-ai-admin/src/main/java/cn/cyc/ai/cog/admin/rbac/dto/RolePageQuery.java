package cn.cyc.ai.cog.admin.rbac.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询参数。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageQuery extends PageQuery {

    /** 关键字：匹配角色编码或名称。 */
    private String keyword;

    /** 状态过滤。 */
    private String status;
}
