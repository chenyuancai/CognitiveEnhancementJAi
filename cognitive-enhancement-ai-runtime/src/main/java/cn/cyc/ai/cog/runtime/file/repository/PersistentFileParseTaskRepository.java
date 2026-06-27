package cn.cyc.ai.cog.runtime.file.repository;

import cn.cyc.ai.cog.runtime.file.domain.FileParseTask;
import cn.cyc.ai.cog.runtime.file.domain.FileParseTaskStatus;
import cn.cyc.ai.cog.runtime.file.entity.FileParseTaskEntity;
import cn.cyc.ai.cog.runtime.file.mapper.FileParseTaskMapper;
import cn.cyc.ai.cog.runtime.file.spi.FileParseTaskRepository;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import cn.cyc.ai.cog.runtime.security.TenantIds;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 持久化文件解析任务仓储。
 *
 * @author cyc
 */
@Repository
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true")
public class PersistentFileParseTaskRepository implements FileParseTaskRepository {

    /**
     * 仓储日志。
     */
    private static final Logger log = LoggerFactory.getLogger(PersistentFileParseTaskRepository.class);

    /**
     * 文件解析任务 Mapper。
     */
    private final FileParseTaskMapper fileParseTaskMapper;

    /**
     * 构造持久化文件解析任务仓储。
     *
     * @param fileParseTaskMapper 文件解析任务 Mapper
     */
    public PersistentFileParseTaskRepository(FileParseTaskMapper fileParseTaskMapper) {
        this.fileParseTaskMapper = fileParseTaskMapper;
    }

    @Override
    public void save(FileParseTask task) {
        fileParseTaskMapper.insert(toEntity(task));
        log.debug("持久化文件解析任务, taskId={}, fileId={}, status={}", task.taskId(), task.fileId(), task.status());
    }

    @Override
    public Optional<FileParseTask> findLatestSucceededByFileId(String fileId) {
        LambdaQueryWrapper<FileParseTaskEntity> queryWrapper = new LambdaQueryWrapper<FileParseTaskEntity>()
                .eq(FileParseTaskEntity::getTenantId, TenantContext.currentTenantId())
                .eq(FileParseTaskEntity::getFileId, fileId)
                .eq(FileParseTaskEntity::getStatus, FileParseTaskStatus.SUCCEEDED.name())
                .orderByDesc(FileParseTaskEntity::getFinishedAt)
                .orderByDesc(FileParseTaskEntity::getRecordedAt)
                .orderByDesc(FileParseTaskEntity::getId)
                .last("LIMIT 1");
        return Optional.ofNullable(fileParseTaskMapper.selectOne(queryWrapper))
                .map(this::toDomain);
    }

    private FileParseTaskEntity toEntity(FileParseTask task) {
        FileParseTaskEntity entity = new FileParseTaskEntity();
        entity.setTenantId(TenantIds.resolveId(task.tenantCode()));
        entity.setTaskId(task.taskId());
        entity.setFileId(task.fileId());
        entity.setStatus(task.status().name());
        entity.setParseResultJson(task.parseResult());
        entity.setErrorMessage(task.errorMessage());
        entity.setStartedAt(task.startedAt());
        entity.setFinishedAt(task.finishedAt());
        entity.setRecordedAt(task.recordedAt());
        return entity;
    }

    private FileParseTask toDomain(FileParseTaskEntity entity) {
        return new FileParseTask(
                TenantIds.toCode(entity.getTenantId()),
                entity.getTaskId(),
                entity.getFileId(),
                FileParseTaskStatus.valueOf(entity.getStatus()),
                entity.getParseResultJson(),
                entity.getErrorMessage(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getRecordedAt()
        );
    }
}
