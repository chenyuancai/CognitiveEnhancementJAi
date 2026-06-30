package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.MembershipLevel;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelPageQuery;
import cn.cyc.ai.cog.platform.membership.dto.MembershipLevelSaveRequest;
import cn.cyc.ai.cog.platform.membership.entity.MembershipLevelEntity;
import cn.cyc.ai.cog.platform.membership.mapper.MembershipLevelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 会员等级仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbMembershipLevelRepository implements MembershipLevelRepository {

    /** 会员等级 Mapper */
    private final MembershipLevelMapper levelMapper;

    /**
     * @param levelMapper 会员等级 Mapper
     */
    public DbMembershipLevelRepository(MembershipLevelMapper levelMapper) {
        this.levelMapper = levelMapper;
    }

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
    @Override
    public PageResult<MembershipLevel> page(MembershipLevelPageQuery query) {
        LambdaQueryWrapper<MembershipLevelEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getSegment())) {
            wrapper.eq(MembershipLevelEntity::getSegment, query.getSegment());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(MembershipLevelEntity::getStatus, query.getStatus());
        }
        wrapper.orderByAsc(MembershipLevelEntity::getSortNo);
        Page<MembershipLevelEntity> page = levelMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 查询是否启用列表。
     *
     * @param segment segment
     * @return 结果列表
     */
    @Override
    public List<MembershipLevel> listEnabled(String segment) {
        LambdaQueryWrapper<MembershipLevelEntity> wrapper = new LambdaQueryWrapper<MembershipLevelEntity>()
                .eq(MembershipLevelEntity::getStatus, CommonConstants.STATUS_ENABLED)
                .orderByAsc(MembershipLevelEntity::getSortNo);
        if (StringUtils.hasText(segment)) {
            wrapper.and(w -> w.eq(MembershipLevelEntity::getSegment, segment).or().eq(MembershipLevelEntity::getSegment, "ALL"));
        }
        return levelMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    /**
     * 查找人ID。
     *
     * @param id 主键 ID
     * @return 查找结果
     */
    @Override
    public MembershipLevel findById(Long id) {
        MembershipLevelEntity entity = levelMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.MEMBERSHIP_LEVEL_NOT_FOUND);
        }
        return toDomain(entity);
    }

    /**
     * 查找人编码。
     *
     * @param levelCode 等级编码
     * @return 查找结果
     */
    @Override
    public MembershipLevel findByCode(String levelCode) {
        MembershipLevel level = findByCodeIfPresent(levelCode);
        if (level == null) {
            throw Errors.of(PlatformErrorCode.MEMBERSHIP_LEVEL_NOT_FOUND, "会员等级不存在：" + levelCode);
        }
        return level;
    }

    /**
     * 查找人编码IfPresent。
     *
     * @param levelCode 等级编码
     * @return 查找结果
     */
    @Override
    public MembershipLevel findByCodeIfPresent(String levelCode) {
        MembershipLevelEntity entity = levelMapper.selectOne(new LambdaQueryWrapper<MembershipLevelEntity>()
                .eq(MembershipLevelEntity::getLevelCode, levelCode)
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    /**
     * 执行require默认ForSegment。
     *
     * @param segment segment
     * @return 执行结果
     */
    @Override
    public MembershipLevel requireDefaultForSegment(String segment) {
        MembershipLevelEntity level = levelMapper.selectOne(new LambdaQueryWrapper<MembershipLevelEntity>()
                .and(w -> w.eq(MembershipLevelEntity::getSegment, segment)
                        .or().eq(MembershipLevelEntity::getSegment, "ALL"))
                .eq(MembershipLevelEntity::getIsDefault, true)
                .eq(MembershipLevelEntity::getStatus, CommonConstants.STATUS_ENABLED)
                .last("LIMIT 1"));
        if (level == null) {
            level = levelMapper.selectOne(new LambdaQueryWrapper<MembershipLevelEntity>()
                    .eq(MembershipLevelEntity::getLevelCode, "FREE")
                    .last("LIMIT 1"));
        }
        if (level == null) {
            throw Errors.of(PlatformErrorCode.MEMBERSHIP_LEVEL_DEFAULT_NOT_FOUND);
        }
        return toDomain(level);
    }

    /**
     * 创建Item。
     *
     * @param request 请求
     * @return 创建结果
     */
    @Override
    public MembershipLevel create(MembershipLevelSaveRequest request) {
        checkCodeUnique(request.getLevelCode(), null);
        MembershipLevelEntity entity = toEntity(request, new MembershipLevelEntity());
        levelMapper.insert(entity);
        return toDomain(entity);
    }

    /**
     * 更新Item。
     *
     * @param id 主键 ID
     * @param request 请求
     * @return 更新结果
     */
    @Override
    public MembershipLevel update(Long id, MembershipLevelSaveRequest request) {
        MembershipLevelEntity entity = levelMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.MEMBERSHIP_LEVEL_NOT_FOUND);
        }
        checkCodeUnique(request.getLevelCode(), id);
        toEntity(request, entity);
        levelMapper.updateById(entity);
        return toDomain(entity);
    }

    /**
     * 执行check编码Unique。
     *
     * @param code 编码
     * @param excludeId excludeID
     */
    private void checkCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<MembershipLevelEntity> wrapper = new LambdaQueryWrapper<MembershipLevelEntity>()
                .eq(MembershipLevelEntity::getLevelCode, code);
        if (excludeId != null) {
            wrapper.ne(MembershipLevelEntity::getId, excludeId);
        }
        if (levelMapper.selectCount(wrapper) > 0) {
            throw Errors.of(PlatformErrorCode.MEMBERSHIP_LEVEL_CODE_EXISTS, "等级编码已存在：" + code);
        }
    }

    /**
     * 转换为实体。
     *
     * @param request 请求
     * @param entity 实体
     * @return 转换结果
     */
    private MembershipLevelEntity toEntity(MembershipLevelSaveRequest request, MembershipLevelEntity entity) {
        entity.setLevelCode(request.getLevelCode());
        entity.setLevelName(request.getLevelName());
        entity.setSegment(request.getSegment());
        entity.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));
        entity.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : CommonConstants.STATUS_ENABLED);
        entity.setBenefitsJson(request.getBenefitsJson());
        return entity;
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private MembershipLevel toDomain(MembershipLevelEntity entity) {
        return new MembershipLevel(
                entity.getId(),
                entity.getLevelCode(),
                entity.getLevelName(),
                entity.getSegment(),
                entity.getIsDefault(),
                entity.getSortNo(),
                entity.getStatus(),
                entity.getBenefitsJson()
        );
    }
}
