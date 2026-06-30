package cn.cyc.ai.cog.auth.user;

import cn.cyc.ai.cog.api.enums.IamUserStatus;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于数据库（qz_iam_user）的用户加载服务。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    /** 认证用户Mapper。 */
    private final AuthUserMapper authUserMapper;

    /**
     * 创建Db用户Details服务。
     *
     * @param authUserMapper 认证用户Mapper
     */
    public DbUserDetailsService(AuthUserMapper authUserMapper) {
        this.authUserMapper = authUserMapper;
    }

    /**
     * 执行load用户人Username。
     *
     * @param username username
     * @return 执行结果
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUserEntity user = authUserMapper.selectOne(new LambdaQueryWrapper<AuthUserEntity>()
                .eq(AuthUserEntity::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        user = resolveBanIfExpired(user);
        if (IamUserStatus.BANNED.matches(user.getStatus())) {
            throw new UsernameNotFoundException("用户已被封禁");
        }
        if (CommonConstants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new UsernameNotFoundException("用户已被停用");
        }
        List<String> roles = authUserMapper.selectRoleCodes(user.getId());
        List<String> permissions = authUserMapper.selectPermissionCodes(user.getId());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));

        String tenantCode = user.getTenantId() == null
                ? CommonConstants.DEFAULT_TENANT
                : authUserMapper.selectTenantCodeById(user.getTenantId());
        if (tenantCode == null) {
            tenantCode = CommonConstants.DEFAULT_TENANT;
        }

        return new AuthUserDetails(
                user.getUsername(),
                user.getPasswordHash(),
                authorities,
                user.getId(),
                user.getTenantId(),
                tenantCode,
                roles,
                permissions.stream().distinct().collect(Collectors.toList()));
    }

    /**
     * 执行resolveBanIfExpired。
     *
     * @param user 用户
     * @return 执行结果
     */
    private AuthUserEntity resolveBanIfExpired(AuthUserEntity user) {
        if (!IamUserStatus.BANNED.matches(user.getStatus()) || user.getBanUntil() == null) {
            return user;
        }
        if (LocalDateTime.now().isAfter(user.getBanUntil())) {
            authUserMapper.update(null, new LambdaUpdateWrapper<AuthUserEntity>()
                    .eq(AuthUserEntity::getId, user.getId())
                    .set(AuthUserEntity::getStatus, CommonConstants.STATUS_ENABLED)
                    .set(AuthUserEntity::getBanUntil, null));
            user.setStatus(CommonConstants.STATUS_ENABLED);
            user.setBanUntil(null);
        }
        return user;
    }
}
