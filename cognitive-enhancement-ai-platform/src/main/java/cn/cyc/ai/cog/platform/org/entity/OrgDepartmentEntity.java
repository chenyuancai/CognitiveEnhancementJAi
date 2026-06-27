package cn.cyc.ai.cog.platform.org.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_acct_org_department")
public class OrgDepartmentEntity extends BaseEntity {

    private Long orgId;
    private Long parentId;
    private String deptName;
    private Integer sortNo;
}
