package cn.cyc.ai.cog.platform.billing.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.dto.OrderPageQuery;
import cn.cyc.ai.cog.platform.billing.entity.OrderEntity;
import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.mapper.OrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 订单 MyBatis 仓储实现。
 */
/**
 * 订单仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbOrderRepository implements OrderRepository {

    /** 订单 Mapper */
    private final OrderMapper orderMapper;

    /**
     * @param orderMapper 订单 Mapper
     */
    public DbOrderRepository(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<Order> page(OrderPageQuery query) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getOrderNo())) {
            wrapper.like(OrderEntity::getOrderNo, query.getOrderNo());
        }
        if (query.getBuyerUserId() != null) {
            wrapper.eq(OrderEntity::getBuyerUserId, query.getBuyerUserId());
        }
        if (query.getAccountId() != null) {
            wrapper.eq(OrderEntity::getAccountId, query.getAccountId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(OrderEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(OrderEntity::getId);
        Page<OrderEntity> page = orderMapper.selectPage(
                Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    /**
     * 执行require人ID。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Override
    public Order requireById(Long id) {
        OrderEntity entity = orderMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.ORDER_NOT_FOUND, "订单不存在：" + id);
        }
        return toDomain(entity);
    }

    /**
     * 执行require人订单No。
     *
     * @param orderNo 订单No
     * @return 执行结果
     */
    @Override
    public Order requireByOrderNo(String orderNo) {
        OrderEntity entity = orderMapper.selectOne(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderNo, orderNo)
                .last("LIMIT 1"));
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.ORDER_NOT_FOUND, "订单不存在：" + orderNo);
        }
        return toDomain(entity);
    }

    /**
     * 执行insert。
     *
     * @param order 订单
     * @return 执行结果
     */
    @Override
    public Order insert(Order order) {
        OrderEntity entity = toEntity(order);
        orderMapper.insert(entity);
        return toDomain(entity);
    }

    /**
     * 更新Item。
     *
     * @param order 订单
     * @return 更新结果
     */
    @Override
    public int update(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity existing = orderMapper.selectById(order.id());
        if (existing != null) {
            entity.setVersion(existing.getVersion());
        }
        return orderMapper.updateById(entity);
    }

    /**
     * 执行sumPaidGmvFen。
     *
     * @param tenantId 租户 ID
     * @param start start
     * @param end end
     * @return 执行结果
     */
    @Override
    public long sumPaidGmvFen(Long tenantId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(OrderEntity::getTenantId, tenantId);
        }
        wrapper.in(OrderEntity::getStatus, OrderStatus.PAID.code(), OrderStatus.FULFILLED.code(), OrderStatus.REFUNDED.code());
        wrapper.ge(OrderEntity::getPayTime, start);
        wrapper.le(OrderEntity::getPayTime, end);
        return orderMapper.selectList(wrapper).stream()
                .mapToLong(order -> order.getAmountFen() == null ? 0L : order.getAmountFen())
                .sum();
    }

    /**
     * 执行数量人状态。
     *
     * @param tenantId 租户 ID
     * @param status 状态
     * @return 执行结果
     */
    @Override
    public long countByStatus(Long tenantId, String status) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(OrderEntity::getTenantId, tenantId);
        }
        wrapper.eq(OrderEntity::getStatus, status);
        return orderMapper.selectCount(wrapper);
    }

    /**
     * 查询PendingCreatedBefore列表。
     *
     * @param deadline deadline
     * @return 结果列表
     */
    @Override
    public java.util.List<Order> listPendingCreatedBefore(java.time.LocalDateTime deadline) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEntity::getStatus, OrderStatus.PENDING.code());
        wrapper.lt(OrderEntity::getCreateTime, deadline);
        return orderMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 执行数量All。
     *
     * @param tenantId 租户 ID
     * @return 执行结果
     */
    @Override
    public long countAll(Long tenantId) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(OrderEntity::getTenantId, tenantId);
        }
        return orderMapper.selectCount(wrapper);
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getTenantId(),
                entity.getOrderNo(),
                entity.getAccountId(),
                entity.getBuyerUserId(),
                entity.getOrderType(),
                entity.getPackageId(),
                entity.getPackageSnapshotJson(),
                entity.getAmountFen(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getPayChannel(),
                entity.getPayTime(),
                entity.getFulfillTime(),
                entity.getIdempotencyKey(),
                entity.getRefundAmountFen(),
                entity.getRefundTime(),
                entity.getRemark()
        );
    }

    /**
     * 转换为实体。
     *
     * @param order 订单
     * @return 转换结果
     */
    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.id());
        entity.setTenantId(order.tenantId());
        entity.setOrderNo(order.orderNo());
        entity.setAccountId(order.accountId());
        entity.setBuyerUserId(order.buyerUserId());
        entity.setOrderType(order.orderType());
        entity.setPackageId(order.packageId());
        entity.setPackageSnapshotJson(order.packageSnapshotJson());
        entity.setAmountFen(order.amountFen());
        entity.setCurrency(order.currency());
        entity.setStatus(order.status());
        entity.setPayChannel(order.payChannel());
        entity.setPayTime(order.payTime());
        entity.setFulfillTime(order.fulfillTime());
        entity.setIdempotencyKey(order.idempotencyKey());
        entity.setRefundAmountFen(order.refundAmountFen());
        entity.setRefundTime(order.refundTime());
        entity.setRemark(order.remark());
        return entity;
    }
}
