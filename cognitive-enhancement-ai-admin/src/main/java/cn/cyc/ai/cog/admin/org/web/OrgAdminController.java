package cn.cyc.ai.cog.admin.org.web;

import cn.cyc.ai.cog.admin.org.assembler.OrgAdminVoAssembler;
import cn.cyc.ai.cog.admin.org.dto.OrgDepartmentVO;
import cn.cyc.ai.cog.admin.org.dto.OrgMemberVO;
import cn.cyc.ai.cog.admin.org.dto.OrganizationVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.account.service.AccountService;
import cn.cyc.ai.cog.platform.org.dto.CreateOrganizationRequest;
import cn.cyc.ai.cog.platform.org.dto.DepartmentSaveRequest;
import cn.cyc.ai.cog.platform.org.dto.OrgMemberSaveRequest;
import cn.cyc.ai.cog.platform.org.dto.OrgPageQuery;
import cn.cyc.ai.cog.platform.org.service.OrgService;
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

/**
 * 管理端组织治理接口：2B/2G 组织、部门与成员。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "账号治理-组织", description = "2B/2G 组织、部门、成员管理")
@RestController
@RequestMapping("/api/admin/orgs")
public class OrgAdminController {

    /** 组织业务服务 */
    private final OrgService orgService;

    /** 账户开通服务 */
    private final AccountService accountService;

    /** Entity → VO 转换器 */
    private final OrgAdminVoAssembler orgAdminVoAssembler;

    /**
     * @param orgService           组织服务
     * @param accountService       账户服务
     * @param orgAdminVoAssembler  VO 转换器
     */
    public OrgAdminController(OrgService orgService,
                              AccountService accountService,
                              OrgAdminVoAssembler orgAdminVoAssembler) {
        this.orgService = orgService;
        this.accountService = accountService;
        this.orgAdminVoAssembler = orgAdminVoAssembler;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "分页查询组织")
    @RequirePermission("admin:user:view")
    @PostMapping("/page")
    public ApiResponse<PageResult<OrganizationVO>> page(@RequestBody OrgPageQuery query) {
        return ApiResponse.success(orgService.page(query).map(orgAdminVoAssembler::toOrgVo));
    }

    /**
     * 执行detail。
     *
     * @param orgId orgID
     * @return 执行结果
     */
    @Operation(summary = "组织详情")
    @RequirePermission("admin:user:view")
    @GetMapping("/{orgId}")
    public ApiResponse<OrganizationVO> detail(@PathVariable Long orgId) {
        return ApiResponse.success(orgAdminVoAssembler.toOrgVo(orgService.detail(orgId)));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "开通组织（2B/2G）", description = "创建独立租户 + 账户 + 组织 + 默认会员额度")
    @RequirePermission("admin:user:update")
    @PostMapping
    public ApiResponse<OrganizationVO> create(@Valid @RequestBody CreateOrganizationRequest request) {
        return ApiResponse.success(orgAdminVoAssembler.toOrgVo(accountService.createOrganization(request)));
    }

    /**
     * 执行departments。
     *
     * @param orgId orgID
     * @return 执行结果
     */
    @Operation(summary = "部门列表")
    @RequirePermission("admin:user:view")
    @GetMapping("/{orgId}/departments")
    public ApiResponse<List<OrgDepartmentVO>> departments(@PathVariable Long orgId) {
        return ApiResponse.success(
                orgService.listDepartments(orgId).stream().map(orgAdminVoAssembler::toDeptVo).toList());
    }

    /**
     * 创建Department。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "新增部门")
    @RequirePermission("admin:user:update")
    @PostMapping("/departments")
    public ApiResponse<OrgDepartmentVO> createDepartment(@Valid @RequestBody DepartmentSaveRequest request) {
        return ApiResponse.success(orgAdminVoAssembler.toDeptVo(orgService.createDepartment(request.getOrgId(), request)));
    }

    /**
     * 更新Department。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "编辑部门")
    @RequirePermission("admin:user:update")
    @PostMapping("/departments/update")
    public ApiResponse<OrgDepartmentVO> updateDepartment(@Valid @RequestBody DepartmentSaveRequest request) {
        return ApiResponse.success(orgAdminVoAssembler.toDeptVo(orgService.updateDepartment(request.getOrgId(), request.getId(), request)));
    }

    /**
     * 删除Department。
     *
     * @param orgId orgID
     * @param deptId deptID
     */
    @Operation(summary = "删除部门")
    @RequirePermission("admin:user:update")
    @DeleteMapping("/{orgId}/departments/{deptId}")
    public ApiResponse<Void> deleteDepartment(@PathVariable Long orgId, @PathVariable Long deptId) {
        orgService.deleteDepartment(orgId, deptId);
        return ApiResponse.success(null);
    }

    /**
     * 执行members。
     *
     * @param orgId orgID
     * @return 执行结果
     */
    @Operation(summary = "成员列表")
    @RequirePermission("admin:user:view")
    @GetMapping("/{orgId}/members")
    public ApiResponse<List<OrgMemberVO>> members(@PathVariable Long orgId) {
        return ApiResponse.success(
                orgService.listMembers(orgId).stream().map(orgAdminVoAssembler::toMemberVo).toList());
    }

    /**
     * 执行addMember。
     *
     * @param request 请求
     * @return 执行结果
     */
    @Operation(summary = "添加成员")
    @RequirePermission("admin:user:update")
    @PostMapping("/members")
    public ApiResponse<OrgMemberVO> addMember(@Valid @RequestBody OrgMemberSaveRequest request) {
        return ApiResponse.success(orgAdminVoAssembler.toMemberVo(orgService.addMember(request.getOrgId(), request)));
    }

    /**
     * 删除Member。
     *
     * @param orgId orgID
     * @param memberId memberID
     */
    @Operation(summary = "移除成员")
    @RequirePermission("admin:user:update")
    @DeleteMapping("/{orgId}/members/{memberId}")
    public ApiResponse<Void> removeMember(@PathVariable Long orgId, @PathVariable Long memberId) {
        orgService.removeMember(orgId, memberId);
        return ApiResponse.success(null);
    }
}
