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

@Repository
public class DbFileRepository implements FileRepository {

    private final FileMapper fileMapper;

    public DbFileRepository(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @Override
    public FileEntity save(FileEntity entity) {
        if (entity.getId() == null) {
            fileMapper.insert(entity);
        } else {
            fileMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public Optional<FileEntity> findById(Long id) {
        return Optional.ofNullable(fileMapper.selectById(id));
    }

    @Override
    public List<FileEntity> findByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return fileMapper.selectList(new LambdaQueryWrapper<FileEntity>().in(FileEntity::getId, ids));
    }

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

    @Override
    public void updateStatus(List<Long> ids, int status) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        fileMapper.update(null, new LambdaUpdateWrapper<FileEntity>()
                .in(FileEntity::getId, ids)
                .set(FileEntity::getStatus, status));
    }

    @Override
    public void deleteById(Long id) {
        FileEntity entity = fileMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.NOT_FOUND, "文件不存在");
        }
        fileMapper.deleteById(id);
    }
}
