package cn.cyc.ai.cog.runtime.service;

import cn.cyc.ai.cog.runtime.domain.ModelCheckRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentModelCheckRecordRepositoryTest {

    @TempDir
    private Path tempDir;

    @Test
    void shouldRestoreModelCheckRecordsFromPersistentFileInLatestFirstOrder() {
        PersistentModelCheckRecordRepository repository = new PersistentModelCheckRecordRepository(tempDir);
        ModelCheckRecord first = record("trace-001", Instant.parse("2026-05-24T10:00:00Z"));
        ModelCheckRecord second = record("trace-002", Instant.parse("2026-05-24T10:01:00Z"));

        repository.save(first);
        repository.save(second);

        PersistentModelCheckRecordRepository restoredRepository = new PersistentModelCheckRecordRepository(tempDir);

        assertThat(restoredRepository.listAll())
                .extracting(ModelCheckRecord::traceId)
                .containsExactly("trace-002", "trace-001");
        assertThat(restoredRepository.listAll().get(0)).isEqualTo(second);
    }

    private static ModelCheckRecord record(String traceId, Instant recordedAt) {
        return new ModelCheckRecord(
                traceId,
                "bailian",
                "qwen-plus",
                true,
                12,
                false,
                null,
                "模型检查成功",
                recordedAt
        );
    }
}
