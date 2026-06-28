package cn.cyc.ai.cog.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("app-it")
class AppPaymentPrepayIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldReturnMockPrepayAndFulfillViaCallback() throws Exception {
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
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andReturn();

        String orderNo = extractJsonString(created.getResponse().getContentAsString(), "orderNo");
        String orderId = extractJsonString(created.getResponse().getContentAsString(), "id");

        mockMvc.perform(post("/api/app/billing/orders/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId": %s, "payChannel": "MOCK"}
                                """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andExpect(jsonPath("$.data.payChannel", is("MOCK")))
                .andExpect(jsonPath("$.data.clientParams.mockPayToken", notNullValue()));

        String statusAfterPay = jdbcTemplate.queryForObject(
                "SELECT status FROM qz_bill_order WHERE order_no = ?", String.class, orderNo);
        org.junit.jupiter.api.Assertions.assertEquals("PENDING", statusAfterPay);

        mockMvc.perform(post("/api/app/billing/pay-callback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "channel": "MOCK",
                                  "orderNo": "%s",
                                  "amountFen": 9900,
                                  "transactionId": "TX-MOCK-PREPAY",
                                  "signature": "dev-mock-secret"
                                }
                                """.formatted(orderNo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("FULFILLED")));
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
