package cn.cyc.ai.cog.app.tutoring.support;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.tutoring.entity.MistakeRecordEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 无 DB 模式下的错题内存存储（IT 用）。
 */
@Component
public class InMemoryMistakeStore {

    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, MistakeRecordEntity> records = new ConcurrentHashMap<>();

    public void save(MistakeRecordEntity entity) {
        if (entity.getId() == null) {
            entity.setId(seq.getAndIncrement());
        }
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(LocalDateTime.now());
        }
        records.put(entity.getId(), entity);
    }

    public PageResult<MistakeRecordEntity> page(Long userId, long current, long size) {
        List<MistakeRecordEntity> all = records.values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .sorted(Comparator.comparing(MistakeRecordEntity::getCreateTime).reversed())
                .toList();
        long page = current < 1 ? 1 : current;
        long pageSize = size < 1 ? 10 : size;
        int from = (int) ((page - 1) * pageSize);
        if (from >= all.size()) {
            return PageResult.empty(page, pageSize);
        }
        int to = (int) Math.min(from + pageSize, all.size());
        return PageResult.of(all.subList(from, to), all.size(), page, pageSize);
    }
}
