package cn.cyc.ai.cog.admin.rbac.entity;

import cn.cyc.ai.cog.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限点 / 菜单实体（映射 qz_iam_permission）。
 *
 * @author cyc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qz_iam_permission")
public class PermissionEntity extends BaseEntity {

    /** 权限点编码，如 iam:role:update。 */
    private String permissionCode;

    /** 前端别名编码，如 admin:role:update。 */
    private String aliasCode;

    /** 权限点名称。 */
    private String permissionName;

    /** 父级权限 ID，顶级为 0。 */
    private Long parentId;

    /** 前端路由路径（菜单）。 */
    private String path;

    /** 前端组件（菜单）。 */
    private String component;

    /** 图标。 */
    private String icon;

    /** 排序号。 */
    private Integer sortNo;

    /** 状态：ENABLED/DISABLED。 */
    private String status;

    /** 作用域：admin / cog。 */
    private String scope;

    /** 类型：menu / action。 */
    private String kind;

    /** 模块键（前端分组）。 */
    private String moduleKey;

    /** 分组键。 */
    private String groupKey;

    /** 绑定菜单键。 */
    private String parentMenuKey;

    /** 描述。 */
    private String description;

    /** 是否内置权限。 */
    private Boolean builtin;
}
