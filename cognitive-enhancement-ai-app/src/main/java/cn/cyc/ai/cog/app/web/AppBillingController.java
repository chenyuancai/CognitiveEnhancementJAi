package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.app.assembler.AppBillingVoAssembler;
import cn.cyc.ai.cog.app.dto.AppBillingPackageQuery;
import cn.cyc.ai.cog.app.dto.AppOrderPageQuery;
import cn.cyc.ai.cog.app.dto.AppPayOrderResultVO;
import cn.cyc.ai.cog.app.dto.OrderVO;
import cn.cyc.ai.cog.app.dto.QuotaPackageVO;
import cn.cyc.ai.cog.app.dto.SubscriptionPackageVO;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.CreateOrderRequest;
import cn.cyc.ai.cog.platform.billing.service.AppOrderService;
import cn.cyc.ai.cog.platform.billing.service.PackageService;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C 端计费接口：可购套餐、下单与支付。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Tag(name = "App-计费", description = "可购套餐、下单与支付")
@RestController
@RequestMapping("/api/app/billing")
public class AppBillingController {

    /** 套餐查询与下单服务 */
    private final PackageService packageService;

    /** C 端订单查询与支付服务 */
    private final AppOrderService appOrderService;

    /** Entity → VO 转换器 */
    private final AppBillingVoAssembler billingVoAssembler;

    /**
     * @param packageService      套餐服务
     * @param appOrderService     C 端订单服务
     * @param billingVoAssembler  VO 转换器
     */
    public AppBillingController(PackageService packageService,
                                AppOrderService appOrderService,
                                AppBillingVoAssembler billingVoAssembler) {
        this.packageService = packageService;
        this.appOrderService = appOrderService;
        this.billingVoAssembler = billingVoAssembler;
    }

    /**
     * 查询在售订阅套餐列表。
     *
     * @param query 客群分段等筛选条件
     * @return 订阅套餐 VO 列表
     */
    @Operation(summary = "在售订阅套餐")
    @SecurityRequirements
    @PostMapping("/subscription-packages/page")
    public ApiResponse<List<SubscriptionPackageVO>> subscriptionPackages(
            @RequestBody(required = false) AppBillingPackageQuery query) {
        String segment = query == null ? null : query.getSegment();
        return ApiResponse.success(packageService.listOnSaleSubscriptionPackages(segment).stream()
                .map(billingVoAssembler::toSubscriptionVo).toList());
    }

    /**
     * 查询在售额度包列表。
     *
     * @param query 客群分段等筛选条件
     * @return 额度包 VO 列表
     */
    @Operation(summary = "在售额度包")
    @SecurityRequirements
    @PostMapping("/quota-packages/page")
    public ApiResponse<List<QuotaPackageVO>> quotaPackages(
            @RequestBody(required = false) AppBillingPackageQuery query) {
        String segment = query == null ? null : query.getSegment();
        return ApiResponse.success(packageService.listOnSaleQuotaPackages(segment).stream()
                .map(billingVoAssembler::toQuotaVo).toList());
    }

    /**
     * 创建订单。
     *
     * @param request 下单请求
     * @return 订单 VO
     */
    @Operation(summary = "创建订单")
    @SecurityRequirements
    @PostMapping("/orders")
    public ApiResponse<OrderVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(billingVoAssembler.toOrderVo(packageService.createOrder(request)));
    }

    /**
     * 分页查询当前用户订单。
     *
     * @param query 分页与状态筛选
     * @return 订单分页结果
     */
    @Operation(summary = "我的订单")
    @SecurityRequirements
    @PostMapping("/orders/page")
    public ApiResponse<PageResult<OrderVO>> myOrders(@RequestBody(required = false) AppOrderPageQuery query) {
        AppOrderPageQuery body = query == null ? new AppOrderPageQuery() : query;
        return ApiResponse.success(appOrderService.myOrders(body.getCurrent(), body.getSize(), body.getStatus())
                .map(billingVoAssembler::toOrderVo));
    }

    /**
     * 查询订单详情。
     *
     * @param id 订单 ID
     * @return 订单 VO
     */
    @Operation(summary = "订单详情")
    @SecurityRequirements
    @GetMapping("/orders/{id}")
    public ApiResponse<OrderVO> orderDetail(@PathVariable Long id) {
        return ApiResponse.success(billingVoAssembler.toOrderVo(appOrderService.myOrderDetail(id)));
    }

    /**
     * 发起支付：返回渠道预下单参数，订单保持待支付，支付结果由回调闭环。
     *
     * @param id      订单 ID
     * @param request 支付请求，可为空（默认 MOCK）
     * @return 预支付参数
     */
    @Operation(summary = "发起支付", description = "返回微信/支付宝/MOCK 客户端调起参数；支付成功后由渠道回调发放权益")
    @SecurityRequirements
    @PostMapping("/orders/pay")
    public ApiResponse<AppPayOrderResultVO> pay(@RequestBody(required = false) AppPayOrderRequest request) {
        AppPayOrderRequest body = request == null ? new AppPayOrderRequest() : request;
        return ApiResponse.success(billingVoAssembler.toPayResultVo(appOrderService.initiatePay(body.getOrderId(), body)));
    }
}
