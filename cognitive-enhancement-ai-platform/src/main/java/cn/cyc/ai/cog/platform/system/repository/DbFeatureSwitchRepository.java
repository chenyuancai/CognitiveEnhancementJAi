package cn.cyc.ai.cog.platform.system.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.system.domain.FeatureSwitch;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchPageQuery;
import cn.cyc.ai.cog.platform.system.dto.FeatureSwitchSaveRequest;
import cn.cyc.ai.cog.platform.system.entity.FeatureSwitchEntity;
import cn.cyc.ai.cog.platform.system.mapper.FeatureSwitchMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 功能开关仓储 MyBatis 实现。
 */
@Repository
public class DbFeatureSwitchRepository implements FeatureSwitchRepository {

    /** 功能开关 Mapper */
    private final FeatureSwitchMapper featureSwitchMapper;

    /**
     * @param featureSwitchMapper 功能开关 Mapper
     */
    public DbFeatureSwitchRepository(FeatureSwitchMapper featureSwitchMapper) {
        this.featureSwitchMapper = featureSwitchMapper;
    }

    @Override
    public PageResult<FeatureSwitch> page(FeatureSwitchPageQuery query) {
        LambdaQueryWrapper<FeatureSwitchEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(FeatureSwitchEntity::getFeatureKey, query.getKeyword())
                    .or().like(FeatureSwitchEntity::getFeatureName, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getSegment())) {
            wrapper.eq(FeatureSwitchEntity::getSegment, query.getSegment());
        }
        wrapper.orderByDesc(FeatureSwitchEntity::getId);
        Page<FeatureSwitchEntity> page = featureSwitchMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public FeatureSwitch save(Long id, FeatureSwitchSaveRequest request) {
        FeatureSwitchEntity entity = id == null ? new FeatureSwitchEntity() : require(id);
        entity.setFeatureKey(request.getFeatureKey().trim());
        entity.setFeatureName(request.getFeatureName().trim());
        entity.setSegment(StringUtils.hasText(request.getSegment()) ? request.getSegment() : "ALL");
        entity.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        entity.setGrayRuleJson(request.getGrayRuleJson());
        if (id == null) {
            featureSwitchMapper.insert(entity);
        } else {
            featureSwitchMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public java.util.List<FeatureSwitch> listEnabled() {
        return featureSwitchMapper.selectList(new LambdaQueryWrapper<FeatureSwitchEntity>()
                        .eq(FeatureSwitchEntity::getEnabled, true)
                        .orderByAsc(FeatureSwitchEntity::getFeatureKey))
                .stream().map(this::toDomain).toList();
    }

    private FeatureSwitchEntity require(Long id) {
        FeatureSwitchEntity entity = featureSwitchMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.FEATURE_SWITCH_NOT_FOUND);
        }
        return entity;
    }

    private FeatureSwitch toDomain(FeatureSwitchEntity entity) {
        return new FeatureSwitch(
                entity.getId(),
                entity.getFeatureKey(),
                entity.getFeatureName(),
                entity.getSegment(),
                entity.getEnabled(),
                entity.getGrayRuleJson()
        );
    }
}
