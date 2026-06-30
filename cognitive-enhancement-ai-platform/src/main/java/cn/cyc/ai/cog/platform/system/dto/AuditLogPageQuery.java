package cn.cyc.ai.cog.platform.system.dto;

import cn.cyc.ai.cog.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AuditLog分页查询条件
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLogPageQuery extends PageQuery {

    /** resource类型。 */
    private String resourceType;
    /** operatorID */
    private Long operatorId;
}
