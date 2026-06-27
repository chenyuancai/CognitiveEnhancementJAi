package cn.cyc.ai.cog.app.web;

import cn.cyc.ai.cog.app.assembler.AppBillingVoAssembler;
import cn.cyc.ai.cog.app.dto.OrderVO;
import cn.cyc.ai.cog.platform.billing.dto.PaymentCallbackRequest;
import cn.cyc.ai.cog.platform.billing.service.PaymentCallbackService;
import cn.cyc.ai.cog.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C 端支付回调接口：接收微信/支付宝/MOCK 支付结果通知。
 */
@Tag(name = "App-支付回调", description = "微信/支付宝/MOCK 支付结果通知")
@RestController
@RequestMapping("/api/app/billing/pay-callback")
public class AppPaymentCallbackController {

    /** 支付回调处理服务 */
    private final PaymentCallbackService paymentCallbackService;

    /** Entity → VO 转换器 */
    private final AppBillingVoAssembler billingVoAssembler;

    /**
     * @param paymentCallbackService 支付回调服务
     * @param billingVoAssembler     VO 转换器
     */
    public AppPaymentCallbackController(PaymentCallbackService paymentCallbackService,
                                        AppBillingVoAssembler billingVoAssembler) {
        this.paymentCallbackService = paymentCallbackService;
        this.billingVoAssembler = billingVoAssembler;
    }

    /**
     * 处理支付渠道回调。
     *
     * @param channel 支付渠道（如 MOCK、WECHAT）
     * @param request 回调请求体
     * @return 履约后订单 VO
     */
    @Operation(summary = "支付结果回调", description = "渠道服务器通知；WECHAT 需 timestamp/nonce + RSA 签名；ALIPAY 需 RSA2 排序串签名；MOCK signature 与 mock-secret 一致。")
    @SecurityRequirements
    @PostMapping
    public ApiResponse<OrderVO> callback(@Valid @RequestBody PaymentCallbackRequest request) {
        return ApiResponse.success(
                billingVoAssembler.toOrderVo(paymentCallbackService.handleCallback(request.getChannel(), request)));
    }
}
