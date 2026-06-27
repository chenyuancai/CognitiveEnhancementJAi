package cn.cyc.ai.cog.app.assembler;

import cn.cyc.ai.cog.app.dto.AppPayOrderResultVO;
import cn.cyc.ai.cog.app.dto.OrderVO;
import cn.cyc.ai.cog.app.dto.QuotaPackageVO;
import cn.cyc.ai.cog.app.dto.SubscriptionPackageVO;
import cn.cyc.ai.cog.platform.billing.domain.Order;
import cn.cyc.ai.cog.platform.billing.domain.QuotaPackage;
import cn.cyc.ai.cog.platform.billing.domain.SubscriptionPackage;
import cn.cyc.ai.cog.platform.billing.dto.PaymentPrepayResult;
import org.springframework.stereotype.Component;

/**
 * 计费域领域对象 → C 端 VO 转换器。
 */
@Component
public class AppBillingVoAssembler {

    /**
     * 订阅套餐领域对象转 C 端 VO。
     *
     * @param pkg 订阅套餐领域对象
     * @return 订阅套餐 VO
     */
    public SubscriptionPackageVO toSubscriptionVo(SubscriptionPackage pkg) {
        SubscriptionPackageVO vo = new SubscriptionPackageVO();
        vo.setId(pkg.id());
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
        return vo;
    }

    /**
     * 额度包领域对象转 C 端 VO。
     *
     * @param pkg 额度包领域对象
     * @return 额度包 VO
     */
    public QuotaPackageVO toQuotaVo(QuotaPackage pkg) {
        QuotaPackageVO vo = new QuotaPackageVO();
        vo.setId(pkg.id());
        vo.setPackageCode(pkg.packageCode());
        vo.setPackageName(pkg.packageName());
        vo.setSegment(pkg.segment());
        vo.setTokenAmount(pkg.tokenAmount());
        vo.setPriceFen(pkg.priceFen());
        vo.setStatus(pkg.status());
        return vo;
    }

    /**
     * 订单领域对象转 C 端 VO。
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

    public AppPayOrderResultVO toPayResultVo(PaymentPrepayResult result) {
        AppPayOrderResultVO vo = new AppPayOrderResultVO();
        vo.setOrderId(result.orderId());
        vo.setOrderNo(result.orderNo());
        vo.setPayChannel(result.payChannel());
        vo.setStatus(result.status());
        vo.setAmountFen(result.amountFen());
        vo.setCurrency(result.currency());
        vo.setPrepayId(result.prepayId());
        vo.setClientParams(result.clientParams());
        return vo;
    }
}
