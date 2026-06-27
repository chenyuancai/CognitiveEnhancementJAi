package cn.cyc.ai.cog.admin.rbac.web;

import cn.cyc.ai.cog.admin.rbac.assembler.RbacAdminVoAssembler;
import cn.cyc.ai.cog.admin.rbac.dto.PermissionCheckCodeRequest;
import cn.cyc.ai.cog.admin.rbac.dto.PermissionSaveRequest;
import cn.cyc.ai.cog.admin.rbac.dto.PermissionTreeGroup;
import cn.cyc.ai.cog.admin.rbac.dto.PermissionVO;
import cn.cyc.ai.cog.admin.rbac.service.PermissionService;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 权限点管理接口（对齐前端 /api/admin/permissions）。
 */
@Tag(name = "系统设置-权限点", description = "权限树与自定义权限 CRUD")
@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionAdminController {

    /** 权限点业务服务 */
    private final PermissionService permissionService;

    /** Entity → VO 转换器 */
    private final RbacAdminVoAssembler rbacAdminVoAssembler;

    /**
     * @param permissionService      权限服务
     * @param rbacAdminVoAssembler   VO 转换器
     */
    public PermissionAdminController(PermissionService permissionService,
                                     RbacAdminVoAssembler rbacAdminVoAssembler) {
        this.permissionService = permissionService;
        this.rbacAdminVoAssembler = rbacAdminVoAssembler;
    }

    @Operation(summary = "权限树", description = "按 scope/moduleKey 分组返回权限点。")
    @RequirePermission("admin:role:update")
    @GetMapping("/tree")
    public ApiResponse<List<PermissionTreeGroup>> tree(@RequestParam(required = false) String scope) {
        return ApiResponse.success(permissionService.buildTree(scope));
    }

    @Operation(summary = "查询全部权限点")
    @RequirePermission("admin:role:update")
    @GetMapping
    public ApiResponse<List<PermissionVO>> listAll() {
        return ApiResponse.success(
                permissionService.listAll().stream().map(rbacAdminVoAssembler::toPermissionVo).toList());
    }

    @Operation(summary = "查询自定义权限点")
    @RequirePermission("admin:permission:create")
    @GetMapping("/custom")
    public ApiResponse<List<PermissionVO>> listCustom() {
        return ApiResponse.success(
                permissionService.listCustom().stream().map(rbacAdminVoAssembler::toPermissionVo).toList());
    }

    @Operation(summary = "校验权限码")
    @RequirePermission("admin:permission:create")
    @PostMapping("/check-code")
    public ApiResponse<Map<String, Object>> checkCode(@RequestBody PermissionCheckCodeRequest request) {
        return ApiResponse.success(permissionService.checkCode(
                request.getCode(), request.getScope(), request.getExcludeId()));
    }

    @Operation(summary = "新增自定义权限点")
    @RequirePermission("admin:permission:create")
    @PostMapping
    public ApiResponse<PermissionVO> create(@Valid @RequestBody PermissionSaveRequest request) {
        return ApiResponse.success(rbacAdminVoAssembler.toPermissionVo(permissionService.create(request)));
    }

    @Operation(summary = "编辑自定义权限点")
    @RequirePermission("admin:permission:update")
    @PostMapping("/update")
    public ApiResponse<PermissionVO> update(@Valid @RequestBody PermissionSaveRequest request) {
        return ApiResponse.success(rbacAdminVoAssembler.toPermissionVo(permissionService.update(request.getId(), request)));
    }

    @Operation(summary = "删除自定义权限点")
    @RequirePermission("admin:permission:delete")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.success(null);
    }
}
