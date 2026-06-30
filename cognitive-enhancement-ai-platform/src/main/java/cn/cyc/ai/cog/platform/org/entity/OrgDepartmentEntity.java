package cn.cyc.ai.cog.platform.org.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OrgDepartment实体
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_org_department")
public class OrgDepartmentEntity extends BaseEntity {

    /** orgID */
    private Long orgId;
    /** parentID */
    private Long parentId;
    /** dept名称。 */
    private String deptName;
    /** sortNo。 */
    private Integer sortNo;
}
