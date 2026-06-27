package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.OrderPageQuery;

/**
 * 订单仓储接口。
 */
public interface OrderRepository {

    /**
     * 分页查询订单。
     *
     * @param query 分页与筛选条件
     * @return 订单分页结果
     */
    PageResult<Order> page(OrderPageQuery query);

    /**
     * 按 ID 查询订单，不存在时抛出业务异常。
     *
     * @param id 订单 ID
     * @return 订单领域对象
     */
    Order requireById(Long id);

    /**
     * 按订单号查询订单，不存在时抛出业务异常。
     *
     * @param orderNo 订单号
     * @return 订单领域对象
     */
    Order requireByOrderNo(String orderNo);

    /**
     * 新增订单并返回持久化后的领域对象。
     *
     * @param order 待插入订单（id 可为 null）
     * @return 持久化后的订单
     */
    Order insert(Order order);

    /**
     * 更新订单，保留数据库乐观锁 version。
     *
     * @param order 待更新订单
     * @return 影响行数
     */
    int update(Order order);

    /**
     * 统计已支付订单 GMV（分），含已支付/已发放/已退款状态。
     *
     * @param tenantId 租户 ID，可为 null
     * @param start    支付时间下限
     * @param end      支付时间上限
     * @return GMV 合计（分）
     */
    long sumPaidGmvFen(Long tenantId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    /**
     * 按状态统计订单数量。
     *
     * @param tenantId 租户 ID，可为 null
     * @param status   订单状态
     * @return 订单数量
     */
    long countByStatus(Long tenantId, String status);

    /**
     * 查询创建时间早于截止时间的待支付订单。
     *
     * @param deadline 创建时间上限（不含之后创建的订单）
     * @return 待支付订单列表
     */
    java.util.List<Order> listPendingCreatedBefore(java.time.LocalDateTime deadline);

    /**
     * 统计订单总数。
     *
     * @param tenantId 租户 ID，可为 null
     * @return 订单总数
     */
    long countAll(Long tenantId);
}
