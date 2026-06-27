package cn.cyc.ai.cog.runtime.harness.repository;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.dto.HarnessReportQuery;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessReportRepository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Harness 报告内存仓储实现，一期不做持久化。
 *
 * @author cyc
 */
@Component
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryHarnessReportRepository implements HarnessReportRepository {

    private static final int MAX_SIZE = 100;

    private final List<HarnessReport> reports = new CopyOnWriteArrayList<>();

    @Override
    public void save(HarnessReport report) {
        reports.add(report);
        if (reports.size() > MAX_SIZE) {
            reports.remove(0);
        }
    }

    @Override
    public Optional<HarnessReport> findById(String harnessId) {
        return reports.stream()
                .filter(r -> r.harnessId().equals(harnessId))
                .findFirst();
    }

    @Override
    public Optional<HarnessReport> findLatest() {
        return reports.stream()
                .max(Comparator.comparing(HarnessReport::startTime));
    }

    @Override
    public List<HarnessReport> findAll() {
        return reports.stream()
                .sorted(Comparator.comparing(HarnessReport::startTime).reversed())
                .toList();
    }

    @Override
    public Page<HarnessReport> findPage(Page<HarnessReport> page) {
        return findPage(page, new HarnessReportQuery(null, null, null));
    }

    @Override
    public Page<HarnessReport> findPage(Page<HarnessReport> page, HarnessReportQuery query) {
        List<HarnessReport> filtered = reports.stream()
                .filter(report -> matchesStatus(report, query))
                .filter(report -> matchesStartTime(report, query))
                .sorted(Comparator.comparing(HarnessReport::startTime).reversed())
                .toList();
        long total = filtered.size();
        int from = (int) ((page.getCurrent() - 1) * page.getSize());
        int to = Math.min(from + (int) page.getSize(), filtered.size());
        List<HarnessReport> records = from < filtered.size() ? filtered.subList(from, to) : List.of();
        Page<HarnessReport> result = new Page<>(page.getCurrent(), page.getSize(), total);
        result.setRecords(records);
        return result;
    }

    private boolean matchesStatus(HarnessReport report, HarnessReportQuery query) {
        if (query == null || query.status() == null || query.status().isBlank()) {
            return true;
        }
        return Objects.equals(report.status(), query.status());
    }

    private boolean matchesStartTime(HarnessReport report, HarnessReportQuery query) {
        if (query == null) {
            return true;
        }
        Instant startTime = report.startTime();
        if (startTime == null) {
            return false;
        }
        if (query.startFrom() != null && startTime.isBefore(query.startFrom())) {
            return false;
        }
        if (query.startTo() != null && startTime.isAfter(query.startTo())) {
            return false;
        }
        return true;
    }
}
