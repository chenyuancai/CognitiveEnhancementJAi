package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限点展示 VO（对齐前端权限管理列表）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class PermissionVO {

    /** 权限点 ID */
    private Long id;

    /** 权限点编码 */
    private String permissionCode;

    /** 前端别名编码 */
    private String aliasCode;

    /** 权限点名称 */
    private String permissionName;

    /** 父级权限 ID */
    private Long parentId;

    /** 前端路由路径 */
    private String path;

    /** 前端组件 */
    private String component;

    /** 图标 */
    private String icon;

    /** 排序号 */
    private Integer sortNo;

    /** 状态 */
    private String status;

    /** 作用域：admin / cog */
    private String scope;

    /** 类型：menu / action */
    private String kind;

    /** 模块键 */
    private String moduleKey;

    /** 分组键 */
    private String groupKey;

    /** 绑定菜单键 */
    private String parentMenuKey;

    /** 描述 */
    private String description;

    /** 是否内置权限 */
    private Boolean builtin;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
