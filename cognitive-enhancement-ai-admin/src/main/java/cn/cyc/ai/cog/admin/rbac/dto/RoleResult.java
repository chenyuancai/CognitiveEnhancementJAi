package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色 API 响应（对齐前端 AdminRole 契约）。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Data
public class RoleResult {

    /** 主键 ID */
    private Long id;
    /** 角色编码。 */
    private String roleCode;
    /** 角色名称。 */
    private String roleName;
    /** 描述。 */
    private String description;
    /** builtin。 */
    private boolean builtin;
    /** 状态。 */
    private String status;
    /** avatarColor。 */
    private String avatarColor;
    /** member数量。 */
    private long memberCount;
    /** 权限Codes。 */
    private List<String> permissionCodes;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
