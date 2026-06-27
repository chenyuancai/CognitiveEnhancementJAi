package cn.cyc.ai.cog.admin.rbac.web;

import cn.cyc.ai.cog.admin.rbac.dto.AssignPermissionRequest;
import cn.cyc.ai.cog.admin.rbac.dto.RoleCheckCodeRequest;
import cn.cyc.ai.cog.admin.rbac.dto.RolePageQuery;
import cn.cyc.ai.cog.admin.rbac.dto.RoleResult;
import cn.cyc.ai.cog.admin.rbac.dto.RoleSaveRequest;
import cn.cyc.ai.cog.admin.rbac.service.RoleService;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 角色管理接口（对齐前端 /api/admin/roles）。
 *
 * @author cyc
 */
@Tag(name = "系统设置-角色管理", description = "角色 CRUD 与权限点授权")
@RestController
@RequestMapping("/api/admin/roles")
public class RoleAdminController {

    private final RoleService roleService;

    public RoleAdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "查询角色列表", description = "返回全部角色及权限点编码。需要 admin:role:update 权限点。")
    @RequirePermission("admin:role:update")
    @GetMapping
    public ApiResponse<List<RoleResult>> list() {
        return ApiResponse.success(roleService.listAll());
    }

    @Operation(summary = "分页查询角色", description = "支持 keyword/status 过滤。")
    @RequirePermission("admin:role:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<RoleResult>> page(@RequestBody RolePageQuery query) {
        return ApiResponse.success(roleService.page(query));
    }

    @Operation(summary = "角色详情")
    @RequirePermission("admin:role:update")
    @GetMapping("/{id}")
    public ApiResponse<RoleResult> detail(@PathVariable Long id) {
        return ApiResponse.success(roleService.getById(id));
    }

    @Operation(summary = "校验角色编码")
    @RequirePermission("admin:role:create")
    @PostMapping("/check-code")
    public ApiResponse<Map<String, Object>> checkCode(@RequestBody RoleCheckCodeRequest request) {
        return ApiResponse.success(roleService.checkCode(request.getCode(), request.getExcludeId()));
    }

    @Operation(summary = "新增角色")
    @RequirePermission("admin:role:create")
    @PostMapping
    public ApiResponse<RoleResult> create(@Valid @RequestBody RoleSaveRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    @Operation(summary = "编辑角色")
    @RequirePermission("admin:role:update")
    @PostMapping("/update")
    public ApiResponse<RoleResult> update(@Valid @RequestBody RoleSaveRequest request) {
        return ApiResponse.success(roleService.update(request.getId(), request));
    }

    @Operation(summary = "删除角色")
    @RequirePermission("admin:role:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success(null);
    }

    @Operation(summary = "角色授权", description = "整体覆盖角色的权限点编码集合。")
    @RequirePermission("admin:role:update")
    @PostMapping("/permissions")
    public ApiResponse<RoleResult> assignPermissions(@Valid @RequestBody AssignPermissionRequest request) {
        return ApiResponse.success(roleService.assignPermissions(request.getRoleId(), request));
    }
}
