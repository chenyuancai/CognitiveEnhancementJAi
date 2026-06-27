package cn.cyc.ai.cog.runtime.file.repository;

import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.spi.FileUploadRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 内存文件上传记录仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryFileUploadRecordRepository implements FileUploadRecordRepository {

    /**
     * 内存上传记录容器。
     */
    private final CopyOnWriteArrayList<FileUploadRecord> records = new CopyOnWriteArrayList<>();

    @Override
    public void save(FileUploadRecord record) {
        records.removeIf(existing -> existing.tenantCode().equals(record.tenantCode())
                && existing.fileId().equals(record.fileId()));
        records.add(record);
    }

    @Override
    public Optional<FileUploadRecord> findByFileId(String fileId) {
        String tenantCode = TenantContext.currentTenantCode();
        return records.stream()
                .filter(record -> tenantCode.equals(record.tenantCode()))
                .filter(record -> fileId.equals(record.fileId()))
                .findFirst();
    }
}
