package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.MarkPaidRequest;
import cn.cyc.ai.cog.platform.billing.dto.OrderPageQuery;
import cn.cyc.ai.cog.platform.billing.dto.RefundRequest;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 订单服务：分页查询、手动标记已支付、手动退款。
 */
@Service
public class OrderService {

    /** 订单仓储 */
    private final OrderRepository orderRepository;

    /** 权益发放服务 */
    private final FulfillmentService fulfillmentService;

    /** 退款逆向服务 */
    private final RefundReversalService refundReversalService;

    /**
     * @param orderRepository         订单仓储
     * @param fulfillmentService      权益发放服务
     * @param refundReversalService   退款逆向服务
     */
    public OrderService(OrderRepository orderRepository,
                        FulfillmentService fulfillmentService,
                        RefundReversalService refundReversalService) {
        this.orderRepository = orderRepository;
        this.fulfillmentService = fulfillmentService;
        this.refundReversalService = refundReversalService;
    }

    /**
     * 分页查询订单。
     *
     * @param query 分页与筛选条件
     * @return 订单分页结果
     */
    public PageResult<Order> page(OrderPageQuery query) {
        return orderRepository.page(query);
    }

    /**
     * 查询订单详情。
     *
     * @param id 订单 ID
     * @return 订单领域对象
     */
    public Order detail(Long id) {
        return orderRepository.requireById(id);
    }

    /**
     * 取消待支付订单。
     *
     * @param id 订单 ID
     * @return 关闭后的订单
     */
    public Order cancel(Long id) {
        Order order = orderRepository.requireById(id);
        if (!OrderStatus.PENDING.matches(order.status())) {
            throw Errors.of(PlatformErrorCode.ORDER_PENDING_ONLY_CANCEL, "仅待支付订单可取消，当前状态：" + order.status());
        }
        Order closed = copyOrder(order,
                OrderStatus.CLOSED.code(),
                order.payChannel(),
                order.payTime(),
                order.fulfillTime(),
                order.refundAmountFen(),
                order.refundTime(),
                order.remark());
        orderRepository.update(closed);
        return closed;
    }

    /**
     * 手动标记订单为已支付并触发权益发放。
     *
     * @param id      订单 ID
     * @param request 标记已支付请求
     * @return 更新后的订单
     */
    public Order markPaid(Long id, MarkPaidRequest request) {
        Order order = orderRepository.requireById(id);
        if (!OrderStatus.PENDING.matches(order.status())) {
            throw Errors.of(PlatformErrorCode.ORDER_PENDING_ONLY_MARK_PAID, "仅待支付订单可标记为已支付，当前状态：" + order.status());
        }
        Order paid = copyOrder(order,
                OrderStatus.PAID.code(),
                StringUtils.hasText(request.getPayChannel()) ? request.getPayChannel() : "MANUAL",
                LocalDateTime.now(),
                order.fulfillTime(),
                order.refundAmountFen(),
                order.refundTime(),
                StringUtils.hasText(request.getRemark()) ? request.getRemark() : order.remark());
        orderRepository.update(paid);
        fulfillmentService.fulfill(order.id());
        return orderRepository.requireById(id);
    }

    /**
     * 手动退款（已发放订单走逆向回收）。
     *
     * @param id      订单 ID
     * @param request 退款请求
     * @return 更新后的订单
     */
    public Order refund(Long id, RefundRequest request) {
        Order order = orderRepository.requireById(id);
        if (!OrderStatus.PAID.matches(order.status()) && !OrderStatus.FULFILLED.matches(order.status())) {
            throw Errors.of(PlatformErrorCode.ORDER_REFUND_STATUS_INVALID, "仅已支付/已发放订单可退款，当前状态：" + order.status());
        }
        long amount = order.amountFen() == null ? 0L : order.amountFen();
        if (request.getRefundAmount() > amount) {
            throw Errors.of(PlatformErrorCode.ORDER_REFUND_AMOUNT_EXCEEDED);
        }
        if (StringUtils.hasText(request.getRemark())) {
            Order withRemark = copyOrder(order,
                    order.status(),
                    order.payChannel(),
                    order.payTime(),
                    order.fulfillTime(),
                    order.refundAmountFen(),
                    order.refundTime(),
                    request.getRemark());
            orderRepository.update(withRemark);
        }
        if (OrderStatus.FULFILLED.matches(order.status())) {
            return refundReversalService.reverse(id, request.getRefundAmount());
        }
        Order refunded = copyOrder(order,
                OrderStatus.REFUNDED.code(),
                order.payChannel(),
                order.payTime(),
                order.fulfillTime(),
                request.getRefundAmount(),
                LocalDateTime.now(),
                StringUtils.hasText(request.getRemark()) ? request.getRemark() : order.remark());
        orderRepository.update(refunded);
        return refunded;
    }

    /**
     * 待支付订单绑定支付渠道（预下单，不改变状态）。
     */
    public Order bindPayChannel(Long id, String payChannel) {
        Order order = orderRepository.requireById(id);
        if (!OrderStatus.PENDING.matches(order.status())) {
            throw Errors.of(PlatformErrorCode.ORDER_PENDING_ONLY_PREPAY);
        }
        Order updated = copyOrder(order,
                order.status(),
                payChannel,
                order.payTime(),
                order.fulfillTime(),
                order.refundAmountFen(),
                order.refundTime(),
                order.remark());
        orderRepository.update(updated);
        return updated;
    }

    private Order copyOrder(Order order,
                            String status,
                            String payChannel,
                            LocalDateTime payTime,
                            LocalDateTime fulfillTime,
                            Long refundAmountFen,
                            LocalDateTime refundTime,
                            String remark) {
        return new Order(
                order.id(),
                order.tenantId(),
                order.orderNo(),
                order.accountId(),
                order.buyerUserId(),
                order.orderType(),
                order.packageId(),
                order.packageSnapshotJson(),
                order.amountFen(),
                order.currency(),
                status,
                payChannel,
                payTime,
                fulfillTime,
                order.idempotencyKey(),
                refundAmountFen,
                refundTime,
                remark
        );
    }
}
