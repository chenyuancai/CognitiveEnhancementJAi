package cn.cyc.ai.cog.base.file.repository;

import cn.cyc.ai.cog.base.api.file.FilePageQuery;
import cn.cyc.ai.cog.base.api.file.FileStatus;
import cn.cyc.ai.cog.base.file.entity.FileEntity;
import cn.cyc.ai.cog.base.file.enums.FileStatusEnum;
import cn.cyc.ai.cog.base.file.mapper.FileMapper;
import cn.cyc.ai.cog.base.file.support.FileConverter;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Db文件仓储
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbFileRepository implements FileRepository {

    /** 文件Mapper。 */
    private final FileMapper fileMapper;

    /**
     * 创建Db文件仓储。
     *
     * @param fileMapper 文件Mapper
     */
    public DbFileRepository(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /**
     * 执行save。
     *
     * @param entity 实体
     * @return 执行结果
     */
    @Override
    public FileEntity save(FileEntity entity) {
        if (entity.getId() == null) {
            fileMapper.insert(entity);
        } else {
            fileMapper.updateById(entity);
        }
        return entity;
    }

    /**
     * 查找人ID。
     *
     * @param id 主键 ID
     * @return 查找结果
     */
    @Override
    public Optional<FileEntity> findById(Long id) {
        return Optional.ofNullable(fileMapper.selectById(id));
    }

    /**
     * 查找人Ids。
     *
     * @param ids ids
     * @return 查找结果
     */
    @Override
    public List<FileEntity> findByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return fileMapper.selectList(new LambdaQueryWrapper<FileEntity>().in(FileEntity::getId, ids));
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<FileEntity> page(FilePageQuery query) {
        int current = query.getCurrent() == null || query.getCurrent() < 1 ? 1 : query.getCurrent();
        int size = query.getSize() == null || query.getSize() < 1 ? 20 : query.getSize();
        LambdaQueryWrapper<FileEntity> wrapper = new LambdaQueryWrapper<>();
        if (query.getTenantId() != null) {
            wrapper.eq(FileEntity::getTenantId, query.getTenantId());
        }
        if (StringUtils.hasText(query.getBizCode())) {
            wrapper.eq(FileEntity::getBizCode, query.getBizCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(FileEntity::getStatus, FileConverter.toDbStatus(query.getStatus()));
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(FileEntity::getOriginalName, query.getKeyword().trim());
        }
        wrapper.orderByDesc(FileEntity::getId);
        Page<FileEntity> page = fileMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), current, size);
    }

    /**
     * 更新状态。
     *
     * @param ids ids
     * @param status 状态
     * @return 更新结果
     */
    @Override
    public void updateStatus(List<Long> ids, int status) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        fileMapper.update(null, new LambdaUpdateWrapper<FileEntity>()
                .in(FileEntity::getId, ids)
                .set(FileEntity::getStatus, status));
    }

    /**
     * 删除人ID。
     *
     * @param id 主键 ID
     */
    @Override
    public void deleteById(Long id) {
        FileEntity entity = fileMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在");
        }
        fileMapper.deleteById(id);
    }
}
