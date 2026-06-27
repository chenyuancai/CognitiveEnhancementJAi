package cn.cyc.ai.cog.admin.rbac.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-权限关联实体（映射 qz_iam_role_permission）。
 *
 * @author cyc
 */
@Data
@TableName("qz_iam_role_permission")
public class RolePermissionEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private Long permissionId;
}
