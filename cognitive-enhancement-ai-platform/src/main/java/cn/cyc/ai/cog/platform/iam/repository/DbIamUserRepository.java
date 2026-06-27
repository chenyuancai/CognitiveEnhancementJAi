package cn.cyc.ai.cog.platform.iam.repository;

import cn.cyc.ai.cog.api.enums.IamUserStatus;
import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import cn.cyc.ai.cog.platform.iam.domain.IamUser;
import cn.cyc.ai.cog.platform.iam.dto.UserPageQuery;
import cn.cyc.ai.cog.platform.iam.entity.SysUserEntity;
import cn.cyc.ai.cog.platform.iam.entity.UserType;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.platform.iam.mapper.IamSysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


/**
 * IAM 用户仓储 MyBatis 实现。
 */
@Repository
public class DbIamUserRepository implements IamUserRepository {

    /** 系统用户 Mapper */
    private final IamSysUserMapper sysUserMapper;

    /**
     * @param sysUserMapper 系统用户 Mapper
     */
    public DbIamUserRepository(IamSysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<IamUser> page(UserPageQuery query) {
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(SysUserEntity::getUsername, query.getKeyword())
                    .or().like(SysUserEntity::getNickname, query.getKeyword())
                    .or().like(SysUserEntity::getEmail, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SysUserEntity::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getUserType())) {
            wrapper.eq(SysUserEntity::getUserType, query.getUserType());
        }
        wrapper.orderByDesc(SysUserEntity::getId);
        Page<SysUserEntity> page = sysUserMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(
                page.getRecords().stream().map(this::toDomain).toList(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());
    }

    @Override
    public IamUser requireById(Long id) {
        SysUserEntity entity = sysUserMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.USER_NOT_FOUND);
        }
        return toDomain(entity);
    }

    @Override
    public IamUser updateStatus(Long id, String status, String banReason, java.time.LocalDateTime banUntil) {
        SysUserEntity user = sysUserMapper.selectById(id);
        if (user == null) {
            throw Errors.of(PlatformErrorCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        user.setBanReason(banReason);
        user.setBanUntil(banUntil);
        if (!IamUserStatus.BANNED.matches(status)) {
            user.setBanReason(null);
            user.setBanUntil(null);
        }
        sysUserMapper.updateById(user);
        return toDomain(user);
    }

    @Override
    public IamUser resolveBanIfExpired(Long id) {
        SysUserEntity user = sysUserMapper.selectById(id);
        if (user == null) {
            throw Errors.of(PlatformErrorCode.USER_NOT_FOUND);
        }
        if (!IamUserStatus.BANNED.matches(user.getStatus()) || user.getBanUntil() == null) {
            return toDomain(user);
        }
        if (java.time.LocalDateTime.now().isAfter(user.getBanUntil())) {
            user.setStatus(CommonConstants.STATUS_ENABLED);
            user.setBanReason(null);
            user.setBanUntil(null);
            sysUserMapper.updateById(user);
        }
        return toDomain(user);
    }

    @Override
    public IamUser findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        SysUserEntity entity = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username.trim())
                .last("LIMIT 1"));
        return entity == null ? null : toDomain(entity);
    }

    @Override
    public IamUser registerCustomer(Long tenantId, String username, String passwordHash,
                                    String nickname, String email, String phone) {
        SysUserEntity user = new SysUserEntity();
        user.setTenantId(tenantId == null ? 1L : tenantId);
        user.setUsername(username.trim());
        user.setPasswordHash(passwordHash);
        user.setNickname(StringUtils.hasText(nickname) ? nickname.trim() : username.trim());
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(CommonConstants.STATUS_ENABLED);
        user.setUserType(UserType.CUSTOMER);
        sysUserMapper.insert(user);
        return toDomain(user);
    }

    @Override
    public void assignRoleByCode(Long userId, String roleCode) {
        Long roleId = sysUserMapper.selectRoleIdByCode(roleCode);
        if (roleId == null) {
            throw Errors.of(PlatformErrorCode.ROLE_NOT_FOUND, "角色不存在：" + roleCode);
        }
        sysUserMapper.insertUserRole(userId, roleId);
    }

    @Override
    public boolean existsUsername(String username) {
        return sysUserMapper.selectCount(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getUsername, username.trim())) > 0;
    }

    @Override
    public boolean existsPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return false;
        }
        return sysUserMapper.selectCount(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getPhone, phone.trim())) > 0;
    }

    @Override
    public boolean existsEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return sysUserMapper.selectCount(new LambdaQueryWrapper<SysUserEntity>()
                .eq(SysUserEntity::getEmail, email.trim())) > 0;
    }

    @Override
    public void bindPrimaryAccountIfAbsent(Long userId, Long accountId) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user != null && user.getPrimaryAccountId() == null) {
            user.setPrimaryAccountId(accountId);
            sysUserMapper.updateById(user);
        }
    }

    @Override
    public void updateTenantAndPrimaryAccount(Long userId, Long tenantId, Long accountId) {
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw Errors.of(PlatformErrorCode.USER_NOT_FOUND);
        }
        user.setTenantId(tenantId);
        user.setPrimaryAccountId(accountId);
        sysUserMapper.updateById(user);
    }

    private IamUser toDomain(SysUserEntity entity) {
        return new IamUser(
                entity.getId(),
                entity.getTenantId(),
                entity.getUsername(),
                entity.getNickname(),
                entity.getAvatarUrl(),
                entity.getStatus(),
                entity.getPrimaryAccountId(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getBanReason(),
                entity.getBanUntil(),
                entity.getUserType(),
                entity.getCreateTime()
        );
    }

    @Override
    public long countUsers(Long tenantId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SysUserEntity::getTenantId, tenantId);
        }
        if (start != null) {
            wrapper.ge(SysUserEntity::getCreateTime, start);
        }
        if (end != null) {
            wrapper.le(SysUserEntity::getCreateTime, end);
        }
        return sysUserMapper.selectCount(wrapper);
    }

    @Override
    public java.util.List<IamUser> listUsersCreatedBetween(Long tenantId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SysUserEntity::getTenantId, tenantId);
        }
        wrapper.ge(SysUserEntity::getCreateTime, start);
        wrapper.le(SysUserEntity::getCreateTime, end);
        return sysUserMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public long countActiveUsers(Long tenantId, int days) {
        java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(Math.max(days, 1) - 1L)
                .toLocalDate().atStartOfDay();
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(SysUserEntity::getTenantId, tenantId);
        }
        wrapper.ge(SysUserEntity::getLastLoginTime, since);
        return sysUserMapper.selectCount(wrapper);
    }

    @Override
    public java.util.List<String> listRoleCodes(Long userId) {
        return sysUserMapper.selectRoleCodes(userId);
    }

    @Override
    public int recoverExpiredBans() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserEntity::getStatus, IamUserStatus.BANNED.code());
        wrapper.isNotNull(SysUserEntity::getBanUntil);
        wrapper.lt(SysUserEntity::getBanUntil, now);
        java.util.List<SysUserEntity> expired = sysUserMapper.selectList(wrapper);
        for (SysUserEntity user : expired) {
            user.setStatus(CommonConstants.STATUS_ENABLED);
            user.setBanReason(null);
            user.setBanUntil(null);
            sysUserMapper.updateById(user);
        }
        return expired.size();
    }
}
