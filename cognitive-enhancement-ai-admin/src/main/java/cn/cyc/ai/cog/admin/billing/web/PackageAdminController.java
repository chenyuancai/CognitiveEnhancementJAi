package cn.cyc.ai.cog.admin.billing.web;

import cn.cyc.ai.cog.admin.billing.assembler.BillingAdminVoAssembler;
import cn.cyc.ai.cog.admin.billing.dto.FinancialRecordPageQuery;
import cn.cyc.ai.cog.admin.billing.dto.FinancialRecordVO;
import cn.cyc.ai.cog.admin.billing.dto.QuotaPackageVO;
import cn.cyc.ai.cog.admin.billing.dto.SubscriptionPackageVO;
import cn.cyc.ai.cog.admin.billing.dto.SubscriptionPageQuery;
import cn.cyc.ai.cog.admin.billing.dto.SubscriptionVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.dto.PackagePageQuery;
import cn.cyc.ai.cog.platform.billing.dto.QuotaPackageSaveRequest;
import cn.cyc.ai.cog.platform.billing.dto.SubscriptionPackageSaveRequest;
import cn.cyc.ai.cog.platform.billing.service.FinancialRecordService;
import cn.cyc.ai.cog.platform.billing.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端计费套餐接口：订阅套餐、额度包、订阅记录与资金流水。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "计费-套餐", description = "订阅套餐与额度包管理")
@RestController
@RequestMapping("/api/admin/billing")
public class PackageAdminController {

    /** 套餐业务服务 */
    private final PackageService packageService;

    /** 资金流水服务 */
    private final FinancialRecordService financialRecordService;

    /** Entity → VO 转换器 */
    private final BillingAdminVoAssembler billingAdminVoAssembler;

    /**
     * @param packageService            套餐服务
     * @param financialRecordService    资金流水服务
     * @param billingAdminVoAssembler   VO 转换器
     */
    public PackageAdminController(PackageService packageService,
                                  FinancialRecordService financialRecordService,
                                  BillingAdminVoAssembler billingAdminVoAssembler) {
        this.packageService = packageService;
        this.financialRecordService = financialRecordService;
        this.billingAdminVoAssembler = billingAdminVoAssembler;
    }

    /**
     * 执行分页SubscriptionPackages。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "订阅套餐分页")
    @RequirePermission("admin:package:update")
    @PostMapping("/subscription-packages/page")
    public ApiResponse<PageResult<SubscriptionPackageVO>> pageSubscriptionPackages(@RequestBody PackagePageQuery query) {
        return ApiResponse.success(
                packageService.pageSubscriptionPackages(query).map(billingAdminVoAssembler::toSubscriptionPackageVo));
    }

    /**
     * 创建SubscriptionPackage。
     * @return 创建结果
     */
    @Operation(summary = "新增订阅套餐")
    @RequirePermission("admin:package:update")
    @PostMapping("/subscription-packages")
    public ApiResponse<SubscriptionPackageVO> createSubscriptionPackage(
            @Valid @RequestBody SubscriptionPackageSaveRequest request) {
        return ApiResponse.success(
                billingAdminVoAssembler.toSubscriptionPackageVo(packageService.saveSubscriptionPackage(null, request)));
    }

    /**
     * 更新SubscriptionPackage。
     * @return 更新结果
     */
    @Operation(summary = "编辑订阅套餐")
    @RequirePermission("admin:package:update")
    @PostMapping("/subscription-packages/update")
    public ApiResponse<SubscriptionPackageVO> updateSubscriptionPackage(
            @Valid @RequestBody SubscriptionPackageSaveRequest request) {
        return ApiResponse.success(
                billingAdminVoAssembler.toSubscriptionPackageVo(packageService.saveSubscriptionPackage(request.getId(), request)));
    }

    /**
     * 执行分页额度Packages。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "额度包分页")
    @RequirePermission("admin:quota-package:update")
    @PostMapping("/quota-packages/page")
    public ApiResponse<PageResult<QuotaPackageVO>> pageQuotaPackages(@RequestBody PackagePageQuery query) {
        return ApiResponse.success(
                packageService.pageQuotaPackages(query).map(billingAdminVoAssembler::toQuotaPackageVo));
    }

    /**
     * 创建额度Package。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Operation(summary = "新增额度包")
    @RequirePermission("admin:quota-package:update")
    @PostMapping("/quota-packages")
    public ApiResponse<QuotaPackageVO> createQuotaPackage(@Valid @RequestBody QuotaPackageSaveRequest request) {
        return ApiResponse.success(
                billingAdminVoAssembler.toQuotaPackageVo(packageService.saveQuotaPackage(null, request)));
    }

    /**
     * 更新额度Package。
     *
     * @param request 请求
     * @return 更新结果
     */
    @Operation(summary = "编辑额度包")
    @RequirePermission("admin:quota-package:update")
    @PostMapping("/quota-packages/update")
    public ApiResponse<QuotaPackageVO> updateQuotaPackage(@Valid @RequestBody QuotaPackageSaveRequest request) {
        return ApiResponse.success(
                billingAdminVoAssembler.toQuotaPackageVo(packageService.saveQuotaPackage(request.getId(), request)));
    }

    /**
     * 执行分页Subscriptions。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "订阅记录分页")
    @RequirePermission("admin:order:update")
    @PostMapping("/subscriptions/page")
    public ApiResponse<PageResult<SubscriptionVO>> pageSubscriptions(@RequestBody SubscriptionPageQuery query) {
        return ApiResponse.success(
                packageService.pageSubscriptions(query.getCurrent(), query.getSize(), query.getAccountId())
                        .map(billingAdminVoAssembler::toSubscriptionVo));
    }

    /**
     * 执行分页FinancialRecords。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Operation(summary = "资金流水分页")
    @RequirePermission("admin:order:update")
    @PostMapping("/financial-records/page")
    public ApiResponse<PageResult<FinancialRecordVO>> pageFinancialRecords(@RequestBody FinancialRecordPageQuery query) {
        return ApiResponse.success(
                financialRecordService.page(query.getCurrent(), query.getSize(), query.getAccountId(), query.getOrderId())
                        .map(billingAdminVoAssembler::toFinancialRecordVo));
    }
}
