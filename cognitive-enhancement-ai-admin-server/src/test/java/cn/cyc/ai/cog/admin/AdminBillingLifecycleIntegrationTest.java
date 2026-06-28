package cn.cyc.ai.cog.admin;

import cn.cyc.ai.cog.api.enums.OrderStatus;
import cn.cyc.ai.cog.platform.billing.service.BillingLifecycleService;
import cn.cyc.ai.cog.platform.knowledge.service.ContentImportJobService;
import cn.cyc.ai.cog.platform.knowledge.dto.ContentImportJobCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("admin-it")
class AdminBillingLifecycleIntegrationTest {

    @Autowired
    private BillingLifecycleService billingLifecycleService;

    @Autowired
    private ContentImportJobService contentImportJobService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCloseExpiredPendingOrders() {
        jdbcTemplate.update("""
                INSERT INTO qz_bill_order (tenant_id, order_no, account_id, buyer_user_id, order_type, amount_fen, status, create_time)
                VALUES (1, 'IT-EXPIRED-001', 1, 1, 'QUOTA', 9900, 'PENDING', TIMESTAMP '2020-01-01 00:00:00')
                """);

        int closed = billingLifecycleService.closeExpiredPendingOrders();
        assertTrue(closed >= 1);

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM qz_bill_order WHERE order_no = 'IT-EXPIRED-001'", String.class);
        assertEquals(OrderStatus.CLOSED.code(), status);
    }

    @Test
    void shouldProcessContentImportJob() {
        ContentImportJobCreateRequest request = new ContentImportJobCreateRequest();
        request.setFileName("it-import.csv");
        request.setFileContent("""
                title,content_type,author,summary,body,min_level_code,tags
                IT导入文章,ARTICLE,作者,摘要,正文内容,FREE,
                """);
        var job = contentImportJobService.create(request);
        contentImportJobService.processNextPendingJob();
        var detail = contentImportJobService.detail(job.id());
        assertEquals("SUCCESS", detail.status());
        assertEquals(1, detail.successCount());
    }
}
