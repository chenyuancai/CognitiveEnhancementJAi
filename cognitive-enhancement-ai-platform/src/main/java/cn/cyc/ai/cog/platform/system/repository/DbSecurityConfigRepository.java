package cn.cyc.ai.cog.platform.system.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.SecurityConfig;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigPageQuery;
import cn.cyc.ai.cog.platform.system.dto.SecurityConfigSaveRequest;
import cn.cyc.ai.cog.platform.system.entity.SecurityConfigEntity;
import cn.cyc.ai.cog.platform.system.mapper.SecurityConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 安全配置仓储 MyBatis 实现。
 */
@Repository
public class DbSecurityConfigRepository implements SecurityConfigRepository {

    /** 安全配置 Mapper */
    private final SecurityConfigMapper securityConfigMapper;

    /**
     * @param securityConfigMapper 安全配置 Mapper
     */
    public DbSecurityConfigRepository(SecurityConfigMapper securityConfigMapper) {
        this.securityConfigMapper = securityConfigMapper;
    }

    @Override
    public PageResult<SecurityConfig> page(SecurityConfigPageQuery query) {
        LambdaQueryWrapper<SecurityConfigEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SecurityConfigEntity::getConfigKey, query.getKeyword());
        }
        wrapper.orderByDesc(SecurityConfigEntity::getId);
        Page<SecurityConfigEntity> page = securityConfigMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public SecurityConfig save(Long id, SecurityConfigSaveRequest request) {
        SecurityConfigEntity entity = id == null ? new SecurityConfigEntity() : require(id);
        entity.setConfigKey(request.getConfigKey().trim());
        entity.setConfigValue(request.getConfigValue());
        entity.setDescription(request.getDescription());
        if (id == null) {
            securityConfigMapper.insert(entity);
        } else {
            securityConfigMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public SecurityConfig findByConfigKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }
        SecurityConfigEntity entity = securityConfigMapper.selectOne(new LambdaQueryWrapper<SecurityConfigEntity>()
                .eq(SecurityConfigEntity::getConfigKey, configKey.trim())
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    private SecurityConfigEntity require(Long id) {
        SecurityConfigEntity entity = securityConfigMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.SECURITY_CONFIG_NOT_FOUND);
        }
        return entity;
    }

    private SecurityConfig toDomain(SecurityConfigEntity entity) {
        return new SecurityConfig(
                entity.getId(),
                entity.getConfigKey(),
                entity.getConfigValue(),
                entity.getDescription()
        );
    }
}
