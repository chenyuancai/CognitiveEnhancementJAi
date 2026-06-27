package cn.cyc.ai.cog.runtime.file.repository;

import cn.cyc.ai.cog.runtime.file.domain.FileUploadRecord;
import cn.cyc.ai.cog.runtime.file.domain.FileUploadStatus;
import cn.cyc.ai.cog.runtime.file.entity.FileUploadRecordEntity;
import cn.cyc.ai.cog.runtime.file.mapper.FileUploadRecordMapper;
import cn.cyc.ai.cog.runtime.file.spi.FileUploadRecordRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 持久化文件上传记录仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentFileUploadRecordRepository implements FileUploadRecordRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentFileUploadRecordRepository.class);

    /**
     * 文件上传记录 Mapper。
     */
    private final FileUploadRecordMapper fileUploadRecordMapper;

    /**
     * 构造持久化文件上传记录仓储。
     *
     * @param fileUploadRecordMapper 文件上传记录 Mapper
     */
    public PersistentFileUploadRecordRepository(FileUploadRecordMapper fileUploadRecordMapper) {
        this.fileUploadRecordMapper = fileUploadRecordMapper;
    }

    @Override
    public void save(FileUploadRecord record) {
        LambdaQueryWrapper<FileUploadRecordEntity> queryWrapper = new LambdaQueryWrapper<FileUploadRecordEntity>()
                .eq(FileUploadRecordEntity::getTenantId, TenantIds.resolveId(record.tenantCode()))
                .eq(FileUploadRecordEntity::getFileId, record.fileId());
        FileUploadRecordEntity existing = fileUploadRecordMapper.selectOne(queryWrapper);
        FileUploadRecordEntity entity = toEntity(record);
        if (existing == null) {
            fileUploadRecordMapper.insert(entity);
            log.debug("持久化新建文件上传记录, fileId={}, fileName={}", record.fileId(), record.fileName());
            return;
        }
        entity.setId(existing.getId());
        fileUploadRecordMapper.updateById(entity);
        log.debug("持久化更新文件上传记录, fileId={}, status={}", record.fileId(), record.status());
    }

    @Override
    public Optional<FileUploadRecord> findByFileId(String fileId) {
        LambdaQueryWrapper<FileUploadRecordEntity> queryWrapper = new LambdaQueryWrapper<FileUploadRecordEntity>()
                .eq(FileUploadRecordEntity::getTenantId, TenantContext.currentTenantId())
                .eq(FileUploadRecordEntity::getFileId, fileId);
        return Optional.ofNullable(fileUploadRecordMapper.selectOne(queryWrapper))
                .map(this::toDomain);
    }

    private FileUploadRecordEntity toEntity(FileUploadRecord record) {
        FileUploadRecordEntity entity = new FileUploadRecordEntity();
        entity.setTenantId(TenantIds.resolveId(record.tenantCode()));
        entity.setFileId(record.fileId());
        entity.setFileName(record.fileName());
        entity.setContentType(record.contentType());
        entity.setSizeBytes(record.sizeBytes());
        entity.setStoragePath(record.storagePath());
        entity.setChecksum(record.checksum());
        entity.setStatus(record.status().name());
        entity.setRecordedAt(record.recordedAt());
        return entity;
    }

    private FileUploadRecord toDomain(FileUploadRecordEntity entity) {
        return new FileUploadRecord(
                TenantIds.toCode(entity.getTenantId()),
                entity.getFileId(),
                entity.getFileName(),
                entity.getContentType(),
                entity.getSizeBytes(),
                entity.getStoragePath(),
                entity.getChecksum(),
                FileUploadStatus.valueOf(entity.getStatus()),
                entity.getRecordedAt()
        );
    }
}
