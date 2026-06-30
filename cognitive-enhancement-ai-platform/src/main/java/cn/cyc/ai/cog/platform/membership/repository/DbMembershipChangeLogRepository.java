package cn.cyc.ai.cog.platform.membership.repository;

import cn.cyc.ai.cog.platform.support.OperationRecordMessages;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.context.UserContext;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.membership.domain.MembershipChangeLog;
import cn.cyc.ai.cog.platform.membership.entity.MembershipChangeLogEntity;
import cn.cyc.ai.cog.platform.membership.mapper.MembershipChangeLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 会员变更日志仓储 MyBatis 实现。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Repository
public class DbMembershipChangeLogRepository implements MembershipChangeLogRepository {

    /** 会员变更日志 Mapper */
    private final MembershipChangeLogMapper changeLogMapper;

    /**
     * @param changeLogMapper 会员变更日志 Mapper
     */
    public DbMembershipChangeLogRepository(MembershipChangeLogMapper changeLogMapper) {
        this.changeLogMapper = changeLogMapper;
    }

    /**
     * 执行分页。
     *
     * @param current current
     * @param size 大小
     * @param accountId 账户ID
     * @return 执行结果
     */
    @Override
    public PageResult<MembershipChangeLog> page(long current, long size, Long accountId) {
        LambdaQueryWrapper<MembershipChangeLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (accountId != null) {
            wrapper.eq(MembershipChangeLogEntity::getAccountId, accountId);
        }
        wrapper.orderByDesc(MembershipChangeLogEntity::getId);
        Page<MembershipChangeLogEntity> page = changeLogMapper.selectPage(Page.of(current, size), wrapper);
        return PageResult.of(page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 执行insert。
     *
     * @param accountId 账户ID
     * @param fromLevel from等级
     * @param toLevel to等级
     * @param changeType change类型
     * @param remark remark
     */
    @Override
    public void insert(Long accountId, String fromLevel, String toLevel, String changeType, String remark) {
        MembershipChangeLogEntity log = new MembershipChangeLogEntity();
        log.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        log.setAccountId(accountId);
        log.setFromLevelCode(fromLevel);
        log.setToLevelCode(toLevel);
        log.setChangeType(changeType);
        String message = OperationRecordMessages.membershipChange(changeType, fromLevel, toLevel, remark);
        log.setMessage(message);
        log.setOperatorId(UserContext.currentUserId());
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        changeLogMapper.insert(log);
    }

    /**
     * 转换为Domain。
     *
     * @param entity 实体
     * @return 转换结果
     */
    private MembershipChangeLog toDomain(MembershipChangeLogEntity entity) {
        return new MembershipChangeLog(
                entity.getId(),
                entity.getTenantId(),
                entity.getAccountId(),
                entity.getFromLevelCode(),
                entity.getToLevelCode(),
                entity.getChangeType(),
                entity.getOperatorId(),
                entity.getMessage(),
                entity.getRemark(),
                entity.getCreateTime()
        );
    }
}
