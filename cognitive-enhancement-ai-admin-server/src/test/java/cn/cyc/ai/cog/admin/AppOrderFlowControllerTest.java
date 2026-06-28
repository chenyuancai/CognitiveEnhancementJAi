package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.infra.web.TraceContextFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * App 下单 → MOCK 回调 → 发放 → Admin 退款 全链路集成测试。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"admin-it"})
class AppOrderFlowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFulfillQuotaOrderAndRefund() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/app/billing/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": 1,
                                  "packageId": 1,
                                  "orderType": "QUOTA"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceContextFilter.TRACE_ID_HEADER))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andExpect(jsonPath("$.data.amountFen", is("9900")))
                .andReturn();

        String body = created.getResponse().getContentAsString();
        String orderNo = extractJsonString(body, "orderNo");
        String orderId = extractJsonString(body, "id");

        mockMvc.perform(post("/api/app/billing/pay-callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "channel": "MOCK",
                                  "orderNo": "%s",
                                  "amountFen": 9900,
                                  "signature": "dev-mock-secret"
                                }
                                """.formatted(orderNo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("FULFILLED")));

        mockMvc.perform(get("/api/admin/quota/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topupRemaining", is("50000")));

        mockMvc.perform(post("/api/admin/billing/orders/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %s,
                                  "refundAmount": 9900,
                                  "remark": "集成测试退款"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("REFUNDED")));

        mockMvc.perform(get("/api/admin/quota/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.topupRemaining", is("0")));
    }

    private String extractJsonString(String json, String field) {
        String marker = "\"" + field + "\":\"";
        int start = json.indexOf(marker);
        if (start >= 0) {
            start += marker.length();
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        }
        marker = "\"" + field + "\":";
        start = json.indexOf(marker);
        if (start < 0) {
            throw new IllegalStateException("字段不存在：" + field);
        }
        start += marker.length();
        int end = json.indexOf(',', start);
        if (end < 0) {
            end = json.indexOf('}', start);
        }
        return json.substring(start, end).replace("\"", "").trim();
    }
}
