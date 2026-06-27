package cn.cyc.ai.cog.platform.operations.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.operations.domain.Banner;
import cn.cyc.ai.cog.platform.operations.dto.BannerPageQuery;
import cn.cyc.ai.cog.platform.operations.dto.BannerSaveRequest;
import cn.cyc.ai.cog.platform.operations.entity.BannerEntity;
import cn.cyc.ai.cog.platform.operations.mapper.BannerMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Banner 仓储 MyBatis 实现。
 */
@Repository
public class DbBannerRepository implements BannerRepository {

    /** Banner Mapper */
    private final BannerMapper bannerMapper;

    /**
     * @param bannerMapper Banner Mapper
     */
    public DbBannerRepository(BannerMapper bannerMapper) {
        this.bannerMapper = bannerMapper;
    }

    @Override
    public PageResult<Banner> page(BannerPageQuery query) {
        LambdaQueryWrapper<BannerEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(BannerEntity::getTitle, query.getKeyword());
        }
        if (StringUtils.hasText(query.getPosition())) {
            wrapper.eq(BannerEntity::getPosition, query.getPosition());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(BannerEntity::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(BannerEntity::getSortNo).orderByDesc(BannerEntity::getId);
        Page<BannerEntity> page = bannerMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public Banner findById(Long id) {
        return toDomain(getExisting(id));
    }

    @Override
    public List<Banner> listActiveByPosition(String position, LocalDateTime now) {
        LocalDateTime effective = now == null ? LocalDateTime.now() : now;
        LambdaQueryWrapper<BannerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BannerEntity::getStatus, CommonConstants.STATUS_ENABLED);
        if (StringUtils.hasText(position)) {
            wrapper.eq(BannerEntity::getPosition, position.trim());
        }
        wrapper.orderByAsc(BannerEntity::getSortNo).orderByDesc(BannerEntity::getId);
        return bannerMapper.selectList(wrapper).stream()
                .map(this::toDomain)
                .filter(banner -> isActive(banner, effective))
                .toList();
    }

    private boolean isActive(Banner banner, LocalDateTime now) {
        if (banner.startTime() != null && banner.startTime().isAfter(now)) {
            return false;
        }
        if (banner.endTime() != null && banner.endTime().isBefore(now)) {
            return false;
        }
        return true;
    }

    @Override
    public Banner create(BannerSaveRequest request) {
        BannerEntity entity = toEntity(request, new BannerEntity());
        bannerMapper.insert(entity);
        return toDomain(entity);
    }

    @Override
    public Banner update(Long id, BannerSaveRequest request) {
        BannerEntity entity = toEntity(request, getExisting(id));
        bannerMapper.updateById(entity);
        return toDomain(entity);
    }

    @Override
    public void delete(Long id) {
        getExisting(id);
        bannerMapper.deleteById(id);
    }

    private BannerEntity toEntity(BannerSaveRequest request, BannerEntity entity) {
        entity.setTitle(request.getTitle().trim());
        entity.setImageUrl(request.getImageUrl().trim());
        entity.setLinkUrl(request.getLinkUrl());
        entity.setPosition(StringUtils.hasText(request.getPosition()) ? request.getPosition() : "HOME_TOP");
        entity.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        entity.setStatus(StringUtils.hasText(request.getStatus())
                ? request.getStatus() : CommonConstants.STATUS_ENABLED);
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        return entity;
    }

    private BannerEntity getExisting(Long id) {
        BannerEntity entity = bannerMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.BANNER_NOT_FOUND, "Banner 不存在：" + id);
        }
        return entity;
    }

    private Banner toDomain(BannerEntity entity) {
        return new Banner(
                entity.getId(),
                entity.getTitle(),
                entity.getImageUrl(),
                entity.getLinkUrl(),
                entity.getPosition(),
                entity.getSortNo(),
                entity.getStatus(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }
}
