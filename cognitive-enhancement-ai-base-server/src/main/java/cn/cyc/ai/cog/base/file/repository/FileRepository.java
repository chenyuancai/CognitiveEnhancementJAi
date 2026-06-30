package cn.cyc.ai.cog.base.file.repository;

import cn.cyc.ai.cog.base.api.file.FilePageQuery;
import cn.cyc.ai.cog.base.file.entity.FileEntity;
import cn.cyc.ai.cog.common.page.PageResult;

import java.util.List;
import java.util.Optional;

/**
 * 文件元数据仓储。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface FileRepository {

    FileEntity save(FileEntity entity);

    Optional<FileEntity> findById(Long id);

    List<FileEntity> findByIds(List<Long> ids);

    PageResult<FileEntity> page(FilePageQuery query);

    void updateStatus(List<Long> ids, int status);

    void deleteById(Long id);
}
