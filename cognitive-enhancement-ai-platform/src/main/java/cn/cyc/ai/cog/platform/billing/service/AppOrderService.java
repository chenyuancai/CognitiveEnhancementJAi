package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.AppPayOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.dto.OrderPageQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * App 端订单服务：我的订单、预下单支付。
 */
@Service
public class AppOrderService {

    private final OrderService orderService;
    private final PaymentChannelService paymentChannelService;

    public AppOrderService(OrderService orderService,
                           PaymentChannelService paymentChannelService) {
        this.orderService = orderService;
        this.paymentChannelService = paymentChannelService;
    }

    public PageResult<Order> myOrders(long current, long size, String status) {
        Long userId = requireUserId();
        OrderPageQuery query = new OrderPageQuery();
        query.setCurrent(current);
        query.setSize(size);
        query.setBuyerUserId(userId);
        query.setStatus(status);
        return orderService.page(query);
    }

    public Order myOrderDetail(Long orderId) {
        Order order = orderService.detail(orderId);
        assertBuyer(order);
        return order;
    }

    /**
     * 发起支付：创建渠道预下单参数，订单保持 PENDING，待回调 markPaid。
     */
    public PaymentPrepayResult initiatePay(Long orderId, AppPayOrderRequest request) {
        Order order = myOrderDetail(orderId);
        if (!OrderStatus.PENDING.matches(order.status())) {
            throw Errors.of(PlatformErrorCode.ORDER_PENDING_ONLY_PAY);
        }
        String channel = StringUtils.hasText(request == null ? null : request.getPayChannel())
                ? request.getPayChannel().trim().toUpperCase()
                : "MOCK";
        orderService.bindPayChannel(orderId, channel);
        Order bound = orderService.detail(orderId);
        return paymentChannelService.createPrepay(bound, request);
    }

    private void assertBuyer(Order order) {
        Long userId = requireUserId();
        if (!userId.equals(order.buyerUserId())) {
            throw Errors.of(PlatformErrorCode.ORDER_ACCESS_FORBIDDEN);
        }
    }

    private Long requireUserId() {
        Long userId = UserContext.currentUserId();
        if (userId == null) {
            throw Errors.of(PlatformErrorCode.NOT_LOGGED_IN);
        }
        return userId;
    }
}
