package cn.cyc.ai.cog.platform.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationRecordMessagesTest {

    @Test
    void shouldPreferOperationSummaryForAudit() {
        String message = OperationRecordMessages.audit("审核内容", "CREATE", "ContentAdminController");
        assertEquals("审核内容", message);
    }

    @Test
    void shouldBuildTokenDeductMessageInChinese() {
        String message = OperationRecordMessages.tokenRecord("DEDUCT", "CYCLE", -100L, "AI_INVOKE");
        assertEquals("AI 调用扣减周期额度 100 Token", message);
    }

    @Test
    void shouldBuildMembershipRegisterMessage() {
        String message = OperationRecordMessages.membershipChange("REGISTER", null, "FREE", null);
        assertEquals("用户注册，开通会员等级 FREE", message);
    }

    @Test
    void shouldBuildFinancialPaymentMessage() {
        String message = OperationRecordMessages.financial("PAYMENT", 9900L, 12L);
        assertTrue(message.contains("订单支付"));
        assertTrue(message.contains("99.00"));
        assertTrue(message.contains("订单 12"));
    }
}
