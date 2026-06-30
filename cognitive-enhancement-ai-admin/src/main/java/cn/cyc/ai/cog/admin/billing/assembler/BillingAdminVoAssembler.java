package cn.cyc.ai.cog.admin.billing.assembler;

import cn.cyc.ai.cog.admin.billing.dto.FinancialRecordVO;
import cn.cyc.ai.cog.admin.billing.dto.OrderVO;
import cn.cyc.ai.cog.admin.billing.dto.QuotaPackageVO;
import cn.cyc.ai.cog.admin.billing.dto.SubscriptionPackageVO;
import cn.cyc.ai.cog.admin.billing.dto.SubscriptionVO;
import cn.cyc.ai.cog.platform.billing.domain.FinancialRecord;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.domain.QuotaPackage;
import cn.cyc.ai.cog.platform.billing.domain.Subscription;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import org.springframework.stereotype.Component;

/**
 * 管理端计费域领域对象 → VO 转换器。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Component
public class BillingAdminVoAssembler {

    /**
     * 订单领域对象转 VO。
     *
     * @param order 订单领域对象
     * @return 订单 VO
     */
    public OrderVO toOrderVo(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.id());
        vo.setTenantId(order.tenantId());
        vo.setOrderNo(order.orderNo());
        vo.setAccountId(order.accountId());
        vo.setBuyerUserId(order.buyerUserId());
        vo.setOrderType(order.orderType());
        vo.setPackageId(order.packageId());
        vo.setPackageSnapshotJson(order.packageSnapshotJson());
        vo.setAmountFen(order.amountFen());
        vo.setCurrency(order.currency());
        vo.setStatus(order.status());
        vo.setPayChannel(order.payChannel());
        vo.setPayTime(order.payTime());
        vo.setFulfillTime(order.fulfillTime());
        vo.setIdempotencyKey(order.idempotencyKey());
        vo.setRefundAmountFen(order.refundAmountFen());
        vo.setRefundTime(order.refundTime());
        vo.setRemark(order.remark());
        return vo;
    }

    /**
     * 订阅套餐领域对象转 VO。
     *
     * @param pkg 订阅套餐领域对象
     * @return 订阅套餐 VO
     */
    public SubscriptionPackageVO toSubscriptionPackageVo(SubscriptionPackage pkg) {
        SubscriptionPackageVO vo = new SubscriptionPackageVO();
        vo.setId(pkg.id());
        vo.setTenantId(pkg.tenantId());
        vo.setPackageCode(pkg.packageCode());
        vo.setPackageName(pkg.packageName());
        vo.setSegment(pkg.segment());
        vo.setLevelId(pkg.levelId());
        vo.setBillingPeriod(pkg.billingPeriod());
        vo.setPeriodCount(pkg.periodCount());
        vo.setTrialDays(pkg.trialDays());
        vo.setPriceFen(pkg.priceFen());
        vo.setOriginalPriceFen(pkg.originalPriceFen());
        vo.setCycleTokenQuota(pkg.cycleTokenQuota());
        vo.setSeatCount(pkg.seatCount());
        vo.setSaleMode(pkg.saleMode());
        vo.setRequireContract(pkg.requireContract());
        vo.setStatus(pkg.status());
        vo.setSnapshotJson(pkg.snapshotJson());
        return vo;
    }

    /**
     * 额度包领域对象转 VO。
     *
     * @param pkg 额度包领域对象
     * @return 额度包 VO
     */
    public QuotaPackageVO toQuotaPackageVo(QuotaPackage pkg) {
        QuotaPackageVO vo = new QuotaPackageVO();
        vo.setId(pkg.id());
        vo.setTenantId(pkg.tenantId());
        vo.setPackageCode(pkg.packageCode());
        vo.setPackageName(pkg.packageName());
        vo.setSegment(pkg.segment());
        vo.setTokenAmount(pkg.tokenAmount());
        vo.setPriceFen(pkg.priceFen());
        vo.setValidDays(pkg.validDays());
        vo.setStatus(pkg.status());
        return vo;
    }

    /**
     * 订阅记录领域对象转 VO。
     *
     * @param sub 订阅记录领域对象
     * @return 订阅记录 VO
     */
    public SubscriptionVO toSubscriptionVo(Subscription sub) {
        SubscriptionVO vo = new SubscriptionVO();
        vo.setId(sub.id());
        vo.setTenantId(sub.tenantId());
        vo.setAccountId(sub.accountId());
        vo.setOrderId(sub.orderId());
        vo.setPackageId(sub.packageId());
        vo.setLevelCode(sub.levelCode());
        vo.setStatus(sub.status());
        vo.setStartAt(sub.startAt());
        vo.setEndAt(sub.endAt());
        vo.setAutoRenew(sub.autoRenew());
        vo.setPackageSnapshotJson(sub.packageSnapshotJson());
        return vo;
    }

    /**
     * 资金流水领域对象转 VO。
     *
     * @param record 资金流水领域对象
     * @return 资金流水 VO
     */
    public FinancialRecordVO toFinancialRecordVo(FinancialRecord record) {
        FinancialRecordVO vo = new FinancialRecordVO();
        vo.setId(record.id());
        vo.setTenantId(record.tenantId());
        vo.setAccountId(record.accountId());
        vo.setOrderId(record.orderId());
        vo.setRecordType(record.recordType());
        vo.setMessage(record.message());
        vo.setAmountFen(record.amountFen());
        vo.setBalanceAfterFen(record.balanceAfterFen());
        vo.setRemark(record.remark());
        vo.setCreateTime(record.createTime());
        return vo;
    }
}
