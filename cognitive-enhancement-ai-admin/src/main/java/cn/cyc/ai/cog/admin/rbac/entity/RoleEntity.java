package cn.cyc.ai.cog.admin.rbac.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体（映射 qz_iam_role）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_iam_role")
public class RoleEntity extends BaseEntity {

    /** 角色编码，唯一。 */
    private String roleCode;

    /** 角色名称。 */
    private String roleName;

    /** 角色描述。 */
    private String description;

    /** 状态：ENABLED/DISABLED。 */
    private String status;

    /** 是否内置角色。 */
    private Boolean builtin;

    /** 头像色（前端展示）。 */
    private String avatarColor;
}
