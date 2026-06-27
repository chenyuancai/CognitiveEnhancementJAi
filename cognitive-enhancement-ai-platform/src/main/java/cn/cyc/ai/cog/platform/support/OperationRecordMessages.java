package cn.cyc.ai.cog.platform.support;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 操作记录中文说明生成器（审计、额度流水、会员变更、资金流水）。
 */
public final class OperationRecordMessages {

    private OperationRecordMessages() {
    }

    /**
     * 管理后台审计：优先使用接口 {@code @Operation.summary}。
     */
    public static String audit(String operationSummary, String action, String resourceType) {
        if (StringUtils.hasText(operationSummary)) {
            return operationSummary.trim();
        }
        String resource = humanizeResource(resourceType);
        return switch (action == null ? "" : action) {
            case "CREATE" -> "新增" + resource;
            case "UPDATE" -> "更新" + resource;
            case "DELETE" -> "删除" + resource;
            default -> "执行" + resource + "操作";
        };
    }

    /**
     * Token 额度流水说明。
     */
    public static String tokenRecord(String recordType, String bucket, long deltaAmount, String bizType) {
        String bucketLabel = bucketLabel(bucket);
        long amount = Math.abs(deltaAmount);
        return switch (recordType == null ? "" : recordType) {
            case "GRANT" -> "发放" + bucketLabel + "额度 " + amount + " Token";
            case "DEDUCT" -> deductMessage(bucketLabel, amount, bizType);
            case "REVOKE" -> "扣回" + bucketLabel + "额度 " + amount + " Token";
            case "ADJUST" -> "管理员调整" + bucketLabel + "额度 " + signed(deltaAmount) + " Token";
            default -> "额度变动 " + signed(deltaAmount) + " Token";
        };
    }

    /**
     * 会员等级变更说明。
     */
    public static String membershipChange(String changeType, String fromLevel, String toLevel, String detail) {
        if (StringUtils.hasText(detail)) {
            return detail.trim();
        }
        String from = StringUtils.hasText(fromLevel) ? fromLevel : "无";
        String to = StringUtils.hasText(toLevel) ? toLevel : "无";
        return switch (changeType == null ? "" : changeType) {
            case "REGISTER" -> "用户注册，开通会员等级 " + to;
            case "MANUAL" -> "手动调整会员等级：" + from + " → " + to;
            case "PURCHASE" -> "购买订阅，会员等级变更为 " + to;
            case "TRIAL_CONVERT" -> "试用转正，会员等级变更为 " + to;
            case "REFUND" -> "订单退款，会员等级回退为 " + to;
            case "EXPIRE" -> "订阅到期，会员等级降为 " + to;
            default -> "会员等级变更：" + from + " → " + to;
        };
    }

    /**
     * 资金流水说明。
     */
    public static String financial(String recordType, Long amountFen, Long orderId) {
        String amountYuan = fenToYuan(amountFen);
        String orderPart = orderId == null ? "" : "（订单 " + orderId + "）";
        return switch (recordType == null ? "" : recordType) {
            case "PAYMENT" -> "订单支付 " + amountYuan + " 元" + orderPart;
            case "REFUND" -> "订单退款 " + amountYuan + " 元" + orderPart;
            default -> "资金流水 " + amountYuan + " 元" + orderPart;
        };
    }

    private static String deductMessage(String bucketLabel, long amount, String bizType) {
        if ("AI_INVOKE".equals(bizType) || "RUNTIME".equals(bizType)) {
            return "AI 调用扣减" + bucketLabel + "额度 " + amount + " Token";
        }
        if ("ORDER".equals(bizType)) {
            return "订单扣减" + bucketLabel + "额度 " + amount + " Token";
        }
        return "扣减" + bucketLabel + "额度 " + amount + " Token";
    }

    private static String bucketLabel(String bucket) {
        if (!StringUtils.hasText(bucket)) {
            return "";
        }
        return switch (bucket.toUpperCase()) {
            case "CYCLE" -> "周期";
            case "GIFT" -> "赠送";
            case "TOPUP" -> "充值";
            default -> bucket;
        };
    }

    private static String humanizeResource(String resourceType) {
        if (!StringUtils.hasText(resourceType)) {
            return "资源";
        }
        String name = resourceType.replace("AdminController", "").replace("Controller", "");
        return switch (name) {
            case "Content" -> "内容";
            case "ContentTag" -> "内容标签";
            case "KnowledgePackage" -> "知识包";
            case "User" -> "用户";
            case "Tenant" -> "租户";
            case "Role" -> "角色";
            case "Permission" -> "权限";
            case "Order" -> "订单";
            case "Banner" -> "Banner";
            case "Announcement" -> "公告";
            case "MessageTemplate" -> "消息模板";
            case "SupportTicket" -> "工单";
            case "MembershipLevel" -> "会员等级";
            case "Org" -> "组织";
            default -> name;
        };
    }

    private static String signed(long delta) {
        return delta > 0 ? "+" + delta : String.valueOf(delta);
    }

    private static String fenToYuan(Long amountFen) {
        if (amountFen == null) {
            return "0.00";
        }
        return BigDecimal.valueOf(Math.abs(amountFen), 2)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }
}
