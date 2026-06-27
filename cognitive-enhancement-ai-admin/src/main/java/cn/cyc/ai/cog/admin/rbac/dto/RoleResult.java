package cn.cyc.ai.cog.admin.rbac.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色 API 响应（对齐前端 AdminRole 契约）。
 *
 * @author cyc
 */
@Data
public class RoleResult {

    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private boolean builtin;
    private String status;
    private String avatarColor;
    private long memberCount;
    private List<String> permissionCodes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
