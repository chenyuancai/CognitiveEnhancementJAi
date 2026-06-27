package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.MarkPaidRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentCallbackRequest;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.billing.support.PaymentChannelSignatureVerifier;
import cn.cyc.ai.cog.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 支付渠道回调服务：验签占位 + 幂等标记已支付。
 */
@Service
public class PaymentCallbackService {

    /** 订单仓储 */
    private final OrderRepository orderRepository;

    /** 订单服务 */
    private final OrderService orderService;

    /** 支付通道签名校验器 */
    private final PaymentChannelSignatureVerifier signatureVerifier;

    /**
     * @param orderRepository     订单仓储
     * @param orderService        订单服务
     * @param signatureVerifier   支付通道签名校验器
     */
    public PaymentCallbackService(OrderRepository orderRepository,
                                  OrderService orderService,
                                  PaymentChannelSignatureVerifier signatureVerifier) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.signatureVerifier = signatureVerifier;
    }

    /**
     * 处理支付渠道回调：验签、幂等校验后标记已支付。
     *
     * @param channel 支付通道标识
     * @param request 回调请求体
     * @return 处理后的订单
     */
    public Order handleCallback(String channel, PaymentCallbackRequest request) {
        String normalizedChannel = normalizeChannel(channel);
        signatureVerifier.verify(normalizedChannel, request);
        Order order = orderRepository.requireByOrderNo(request.getOrderNo());
        if (OrderStatus.PAID.matches(order.status()) || OrderStatus.FULFILLED.matches(order.status())) {
            return order;
        }
        if (!OrderStatus.PENDING.matches(order.status())) {
            throw Errors.of(PlatformErrorCode.ORDER_STATUS_NOT_PAYABLE, "订单状态不可支付：" + order.status());
        }
        if (request.getAmountFen() != null && order.amountFen() != null
                && !request.getAmountFen().equals(order.amountFen())) {
            throw Errors.of(PlatformErrorCode.ORDER_CALLBACK_AMOUNT_MISMATCH);
        }
        MarkPaidRequest markPaid = new MarkPaidRequest();
        markPaid.setPayChannel(normalizedChannel);
        markPaid.setRemark(StringUtils.hasText(request.getRemark())
                ? request.getRemark()
                : "callback:" + request.getTransactionId());
        return orderService.markPaid(order.id(), markPaid);
    }

    private String normalizeChannel(String channel) {
        if (!StringUtils.hasText(channel)) {
            throw Errors.of(PlatformErrorCode.PAYMENT_CHANNEL_EMPTY);
        }
        return channel.trim().toUpperCase();
    }
}
