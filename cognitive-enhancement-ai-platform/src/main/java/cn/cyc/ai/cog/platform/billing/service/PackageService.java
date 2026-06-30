package cn.cyc.ai.cog.platform.billing.service;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.domain.QuotaPackage;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.platform.billing.dto.CreateOrderRequest;
import cn.cyc.ai.cog.platform.billing.dto.PackagePageQuery;
import cn.cyc.ai.cog.platform.billing.dto.QuotaPackageSaveRequest;
import cn.cyc.ai.cog.platform.billing.dto.SubscriptionPackageSaveRequest;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.repository.OrderRepository;
import cn.cyc.ai.cog.platform.billing.repository.QuotaPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionPackageRepository;
import cn.cyc.ai.cog.platform.billing.repository.SubscriptionRepository;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 套餐与订阅查询、下单编排服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class PackageService {

    /** 订阅套餐仓储 */
    private final SubscriptionPackageRepository subscriptionPackageRepository;

    /** 额度包仓储 */
    private final QuotaPackageRepository quotaPackageRepository;

    /** 订阅记录仓储 */
    private final SubscriptionRepository subscriptionRepository;

    /** 订单仓储 */
    private final OrderRepository orderRepository;

    /**
     * @param subscriptionPackageRepository 订阅套餐仓储
     * @param quotaPackageRepository        额度包仓储
     * @param subscriptionRepository        订阅记录仓储
     * @param orderRepository               订单仓储
     */
    public PackageService(SubscriptionPackageRepository subscriptionPackageRepository,
                          QuotaPackageRepository quotaPackageRepository,
                          SubscriptionRepository subscriptionRepository,
                          OrderRepository orderRepository) {
        this.subscriptionPackageRepository = subscriptionPackageRepository;
        this.quotaPackageRepository = quotaPackageRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 分页查询订阅套餐。
     *
     * @param query 分页与筛选条件
     * @return 订阅套餐分页结果
     */
    public PageResult<SubscriptionPackage> pageSubscriptionPackages(PackagePageQuery query) {
        return subscriptionPackageRepository.page(query);
    }

    /**
     * 分页查询额度包。
     *
     * @param query 分页与筛选条件
     * @return 额度包分页结果
     */
    public PageResult<QuotaPackage> pageQuotaPackages(PackagePageQuery query) {
        return quotaPackageRepository.page(query);
    }

    /**
     * 查询在售订阅套餐列表。
     *
     * @param segment 客群分段，可为空表示不限
     * @return 在售订阅套餐列表
     */
    public List<SubscriptionPackage> listOnSaleSubscriptionPackages(String segment) {
        return subscriptionPackageRepository.listOnSale(segment);
    }

    /**
     * 查询在售额度包列表。
     *
     * @param segment 客群分段，可为空表示不限
     * @return 在售额度包列表
     */
    public List<QuotaPackage> listOnSaleQuotaPackages(String segment) {
        return quotaPackageRepository.listOnSale(segment);
    }

    /**
     * 保存订阅套餐（新建或更新）。
     *
     * @param id      套餐 ID，新建时为 null
     * @param request 保存请求
     * @return 持久化后的订阅套餐
     */
    public SubscriptionPackage saveSubscriptionPackage(Long id, SubscriptionPackageSaveRequest request) {
        return subscriptionPackageRepository.save(id, request);
    }

    /**
     * 保存额度包（新建或更新）。
     *
     * @param id      套餐 ID，新建时为 null
     * @param request 保存请求
     * @return 持久化后的额度包
     */
    public QuotaPackage saveQuotaPackage(Long id, QuotaPackageSaveRequest request) {
        return quotaPackageRepository.save(id, request);
    }

    /**
     * 分页查询账户订阅记录。
     *
     * @param current   当前页
     * @param size      每页大小
     * @param accountId 商业账户 ID
     * @return 订阅记录分页结果
     */
    public PageResult<Subscription> pageSubscriptions(long current, long size, Long accountId) {
        return subscriptionRepository.page(current, size, accountId);
    }

    /**
     * 创建待支付订单。
     *
     * @param request 下单请求
     * @return 持久化后的订单
     */
    public Order createOrder(CreateOrderRequest request) {
        Long buyerUserId = UserContext.currentUserId();
        if (buyerUserId == null) {
            throw Errors.of(PlatformErrorCode.NOT_LOGGED_IN);
        }
        long amountFen;
        if ("SUBSCRIPTION".equals(request.getOrderType())) {
            amountFen = subscriptionPackageRepository.requireById(request.getPackageId()).priceFen();
        } else if ("QUOTA".equals(request.getOrderType())) {
            amountFen = quotaPackageRepository.requireById(request.getPackageId()).priceFen();
        } else {
            throw Errors.of(PlatformErrorCode.ORDER_TYPE_INVALID);
        }
        String orderNo = "O" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
        Order order = new Order(
                null,
                null,
                orderNo,
                request.getAccountId(),
                buyerUserId,
                request.getOrderType(),
                request.getPackageId(),
                null,
                amountFen,
                "CNY",
                OrderStatus.PENDING.code(),
                null,
                null,
                null,
                "create:" + orderNo,
                null,
                null,
                null
        );
        return orderRepository.insert(order);
    }
}
