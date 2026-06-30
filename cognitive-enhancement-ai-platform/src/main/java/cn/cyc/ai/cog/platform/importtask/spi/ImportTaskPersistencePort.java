package cn.cyc.ai.cog.platform.importtask.spi;

import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.importtask.entity.ImportTaskEntity;

import java.util.Optional;

/**
 * 导入任务持久化端口。
 */
public interface ImportTaskPersistencePort {

    ImportTaskEntity save(ImportTaskEntity task);

    void update(ImportTaskEntity task);

    Optional<ImportTaskEntity> findByCode(Long tenantId, Long userId, String taskCode);

    Optional<ImportTaskEntity> findById(Long tenantId, Long userId, Long id);

    PageResult<ImportTaskEntity> page(Long tenantId, Long userId, long current, long size);

    Optional<ImportTaskEntity> findLatestActive(Long tenantId, Long userId);
}
