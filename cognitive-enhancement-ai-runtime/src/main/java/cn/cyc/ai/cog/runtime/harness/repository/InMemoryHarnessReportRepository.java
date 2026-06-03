package cn.cyc.ai.cog.runtime.harness.repository;

import cn.cyc.ai.cog.runtime.harness.domain.HarnessReport;
import cn.cyc.ai.cog.runtime.harness.spi.HarnessReportRepository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Harness 报告内存仓储实现，一期不做持久化。
 *
 * @author cyc
 */
@Component
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
        List<HarnessReport> all = findAll();
        long total = all.size();
        int from = (int) ((page.getCurrent() - 1) * page.getSize());
        int to = Math.min(from + (int) page.getSize(), all.size());
        List<HarnessReport> records = from < all.size() ? all.subList(from, to) : List.of();
        Page<HarnessReport> result = new Page<>(page.getCurrent(), page.getSize(), total);
        result.setRecords(records);
        return result;
    }
}
