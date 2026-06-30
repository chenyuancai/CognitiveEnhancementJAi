package cn.cyc.ai.cog.platform.importtask.service;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.importtask.entity.ImportTaskEntity;
import cn.cyc.ai.cog.platform.importtask.mapper.ImportTaskMapper;
import cn.cyc.ai.cog.platform.importtask.spi.ImportTaskPersistencePort;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
public class DbImportTaskPersistenceService implements ImportTaskPersistencePort {

    private final ImportTaskMapper importTaskMapper;

    public DbImportTaskPersistenceService(ImportTaskMapper importTaskMapper) {
        this.importTaskMapper = importTaskMapper;
    }

    @Override
    public ImportTaskEntity save(ImportTaskEntity task) {
        importTaskMapper.insert(task);
        return task;
    }

    @Override
    public void update(ImportTaskEntity task) {
        importTaskMapper.updateById(task);
    }

    @Override
    public Optional<ImportTaskEntity> findByCode(Long tenantId, Long userId, String taskCode) {
        return Optional.ofNullable(importTaskMapper.selectOne(new LambdaQueryWrapper<ImportTaskEntity>()
                .eq(ImportTaskEntity::getTenantId, tenantId)
                .eq(ImportTaskEntity::getUserId, userId)
                .eq(ImportTaskEntity::getTaskCode, taskCode)));
    }

    @Override
    public Optional<ImportTaskEntity> findById(Long tenantId, Long userId, Long id) {
        ImportTaskEntity entity = importTaskMapper.selectById(id);
        if (entity == null || !tenantId.equals(entity.getTenantId()) || !userId.equals(entity.getUserId())) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public PageResult<ImportTaskEntity> page(Long tenantId, Long userId, long current, long size) {
        Page<ImportTaskEntity> page = importTaskMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<ImportTaskEntity>()
                        .eq(ImportTaskEntity::getTenantId, tenantId)
                        .eq(ImportTaskEntity::getUserId, userId)
                        .orderByDesc(ImportTaskEntity::getCreateTime));
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public Optional<ImportTaskEntity> findLatestActive(Long tenantId, Long userId) {
        return Optional.ofNullable(importTaskMapper.selectOne(new LambdaQueryWrapper<ImportTaskEntity>()
                .eq(ImportTaskEntity::getTenantId, tenantId)
                .eq(ImportTaskEntity::getUserId, userId)
                .in(ImportTaskEntity::getStatus, "pending", "processing")
                .orderByDesc(ImportTaskEntity::getUpdateTime)
                .last("LIMIT 1")));
    }
}
