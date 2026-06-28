package cn.cyc.ai.cog.runtime.harness.repository;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessReportQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Harness 报告内存仓储测试。
 *
 * @author cyc
 */
class InMemoryHarnessReportRepositoryTest {

    private InMemoryHarnessReportRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHarnessReportRepository();
        repository.save(report("HAR-001", "PASSED", Instant.parse("2026-06-01T10:00:00Z")));
        repository.save(report("HAR-002", "FAILED", Instant.parse("2026-06-02T10:00:00Z")));
        repository.save(report("HAR-003", "PASSED", Instant.parse("2026-06-03T10:00:00Z")));
    }

    @Test
    void findPage_shouldFilterByStatus() {
        HarnessReportQuery query = new HarnessReportQuery("FAILED", null, null);

        Page<HarnessReport> page = repository.findPage(new Page<>(1, 10), query);

        assertEquals(1, page.getTotal());
        assertEquals("HAR-002", page.getRecords().get(0).harnessId());
    }

    @Test
    void findPage_shouldFilterByStartTimeRange() {
        HarnessReportQuery query = new HarnessReportQuery(
                null,
                Instant.parse("2026-06-02T00:00:00Z"),
                Instant.parse("2026-06-03T00:00:00Z"));

        Page<HarnessReport> page = repository.findPage(new Page<>(1, 10), query);

        assertEquals(1, page.getTotal());
        assertEquals("HAR-002", page.getRecords().get(0).harnessId());
    }

    @Test
    void findPage_shouldSortByStartTimeDesc() {
        Page<HarnessReport> page = repository.findPage(new Page<>(1, 10), new HarnessReportQuery(null, null, null));

        assertEquals(3, page.getTotal());
        assertEquals("HAR-003", page.getRecords().get(0).harnessId());
        assertEquals("HAR-001", page.getRecords().get(2).harnessId());
    }

    private static HarnessReport report(String harnessId, String status, Instant startTime) {
        return new HarnessReport(
                harnessId, "trace-" + harnessId, status, startTime, startTime,
                100L, null, List.of(), null);
    }
}
