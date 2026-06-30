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
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
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

    /**
     * 执行save。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 执行结果
     */
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

    /**
     * 查找人配置键。
     *
     * @param configKey 配置键
     * @return 查找结果
     */
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

    /**
     * 执行require。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    private SecurityConfigEntity require(Long id) {
        SecurityConfigEntity entity = securityConfigMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.SECURITY_CONFIG_NOT_FOUND);
        }
        return entity;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private SecurityConfig toDomain(SecurityConfigEntity entity) {
        return new SecurityConfig(
                entity.getId(),
                entity.getConfigKey(),
                entity.getConfigValue(),
                entity.getDescription()
        );
    }
}
