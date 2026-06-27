package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.runtime.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色数据库实体。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_iam_role")
public class SysRoleEntity extends BaseEntity {

    private String roleCode;
    private String roleName;
    private String description;
    private String status;
}
