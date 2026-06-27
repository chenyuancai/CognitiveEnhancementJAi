package cn.cyc.ai.cog.admin.billing.web;

import cn.cyc.ai.cog.admin.billing.assembler.BillingAdminVoAssembler;
import cn.cyc.ai.cog.admin.billing.dto.OrderVO;
import cn.cyc.ai.cog.admin.security.RequirePermission;
import cn.cyc.ai.cog.api.response.ApiResponse;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.api.request.IdentifiedCommand;
import cn.cyc.ai.cog.platform.billing.dto.MarkPaidRequest;
import cn.cyc.ai.cog.platform.billing.dto.OrderPageQuery;
import cn.cyc.ai.cog.platform.billing.dto.RefundRequest;
import cn.cyc.ai.cog.platform.billing.service.OrderService;
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
 * 订单管理接口（计费-订单）。
 *
 * <p>当前不接真实支付网关：支付通过「手动标记已支付」，退款通过「手动退款」完成。</p>
 */
@Tag(name = "计费-订单管理", description = "订单查询、手动标记已支付、手动退款")
@RestController
@RequestMapping("/api/admin/billing/orders")
public class OrderAdminController {

    /** 订单业务服务 */
    private final OrderService orderService;

    /** Entity → VO 转换器 */
    private final BillingAdminVoAssembler billingAdminVoAssembler;

    /**
     * @param orderService             订单服务
     * @param billingAdminVoAssembler  VO 转换器
     */
    public OrderAdminController(OrderService orderService, BillingAdminVoAssembler billingAdminVoAssembler) {
        this.orderService = orderService;
        this.billingAdminVoAssembler = billingAdminVoAssembler;
    }

    @Operation(summary = "分页查询订单", description = "支持 orderNo/userId/status 过滤。需要 admin:order:update 权限点。")
    @RequirePermission("admin:order:update")
    @PostMapping("/page")
    public ApiResponse<PageResult<OrderVO>> page(@RequestBody OrderPageQuery query) {
        return ApiResponse.success(orderService.page(query).map(billingAdminVoAssembler::toOrderVo));
    }

    @Operation(summary = "订单详情", description = "需要 admin:order:update 权限点。")
    @RequirePermission("admin:order:update")
    @GetMapping("/{id}")
    public ApiResponse<OrderVO> detail(@PathVariable Long id) {
        return ApiResponse.success(billingAdminVoAssembler.toOrderVo(orderService.detail(id)));
    }

    @Operation(summary = "手动标记已支付", description = "仅待支付订单可操作。需要 admin:order:update 权限点。")
    @RequirePermission("admin:order:update")
    @PostMapping("/mark-paid")
    public ApiResponse<OrderVO> markPaid(@RequestBody MarkPaidRequest request) {
        return ApiResponse.success(billingAdminVoAssembler.toOrderVo(orderService.markPaid(request.getOrderId(), request)));
    }

    @Operation(summary = "取消待支付订单", description = "仅待支付订单可取消为 CLOSED。需要 admin:order:update 权限点。")
    @RequirePermission("admin:order:update")
    @PostMapping("/cancel")
    public ApiResponse<OrderVO> cancel(@Valid @RequestBody IdentifiedCommand command) {
        return ApiResponse.success(billingAdminVoAssembler.toOrderVo(orderService.cancel(command.id())));
    }

    @Operation(summary = "手动退款", description = "仅已支付订单可操作。需要 admin:order:refund 权限点。")
    @RequirePermission("admin:order:refund")
    @PostMapping("/refund")
    public ApiResponse<OrderVO> refund(@Valid @RequestBody RefundRequest request) {
        return ApiResponse.success(billingAdminVoAssembler.toOrderVo(orderService.refund(request.getOrderId(), request)));
    }
}
