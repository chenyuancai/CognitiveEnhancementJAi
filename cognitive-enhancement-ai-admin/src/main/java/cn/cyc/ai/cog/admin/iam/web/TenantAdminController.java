package cn.cyc.ai.cog.admin.iam.web;

import cn.cyc.ai.cog.admin.iam.assembler.IamAdminVoAssembler;
import cn.cyc.ai.cog.admin.iam.dto.TenantStatusUpdateRequest;
import cn.cyc.ai.cog.admin.iam.dto.TenantVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.iam.dto.TenantPageQuery;
import cn.cyc.ai.cog.platform.iam.dto.TenantSaveRequest;
import cn.cyc.ai.cog.platform.iam.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IAM 租户管理接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "IAM-租户", description = "平台租户查询与维护")
@RestController
@RequestMapping("/api/admin/iam/tenants")
public class TenantAdminController {

    /** 租户业务服务 */
    private final TenantService tenantService;

    /** Entity → VO 转换器 */
    private final IamAdminVoAssembler iamAdminVoAssembler;

    /**
     * @param tenantService         租户服务
     * @param iamAdminVoAssembler   VO 转换器
     */
    public TenantAdminController(TenantService tenantService, IamAdminVoAssembler iamAdminVoAssembler) {
        this.tenantService = tenantService;
        this.iamAdminVoAssembler = iamAdminVoAssembler;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "分页查询租户")
    @RequirePermission("admin:user:view")
    @PostMapping("/page")
    public ApiResponse<PageResult<TenantVO>> page(@RequestBody TenantPageQuery query) {
        return ApiResponse.success(tenantService.page(query).map(iamAdminVoAssembler::toTenantVo));
    }

    /**
     * 执行detail。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Operation(summary = "租户详情")
    @RequirePermission("admin:user:view")
    @GetMapping("/{id}")
    public ApiResponse<TenantVO> detail(@PathVariable Long id) {
        return ApiResponse.success(iamAdminVoAssembler.toTenantVo(tenantService.detail(id)));
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "创建租户")
    @RequirePermission("admin:user:update")
    @PostMapping
    public ApiResponse<TenantVO> create(@Valid @RequestBody TenantSaveRequest request) {
        return ApiResponse.success(iamAdminVoAssembler.toTenantVo(tenantService.create(request)));
    }

    /**
     * 更新Item。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "更新租户")
    @RequirePermission("admin:user:update")
    @PostMapping("/update")
    public ApiResponse<TenantVO> update(@Valid @RequestBody TenantSaveRequest request) {
        return ApiResponse.success(iamAdminVoAssembler.toTenantVo(tenantService.update(request.getId(), request)));
    }

    /**
     * 更新状态。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "更新租户状态")
    @RequirePermission("admin:user:update")
    @PostMapping("/status")
    public ApiResponse<TenantVO> updateStatus(@RequestBody TenantStatusUpdateRequest request) {
        return ApiResponse.success(iamAdminVoAssembler.toTenantVo(tenantService.updateStatus(request.getId(), request.getStatus())));
    }
}
