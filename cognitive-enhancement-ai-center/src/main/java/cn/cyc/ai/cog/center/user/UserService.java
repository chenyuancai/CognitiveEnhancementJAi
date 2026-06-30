package cn.cyc.ai.cog.center.user;

import cn.cyc.ai.cog.common.spi.AccountProvisioner;
import cn.cyc.ai.cog.runtime.security.TenantContext;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理服务，提供注册、登录、查询等核心操作。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
@Service
public class UserService {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /** 用户Mapper。 */
    private final SysUserMapper userMapper;
    /** 角色Mapper。 */
    private final SysRoleMapper roleMapper;
    /** 用户角色Mapper。 */
    private final SysUserRoleMapper userRoleMapper;
    /** 账户Provisioner。 */
    private final ObjectProvider<AccountProvisioner> accountProvisioner;
    /** 密码Encoder。 */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 创建用户服务。
     */
    public UserService(SysUserMapper userMapper,
                     SysRoleMapper roleMapper,
                     SysUserRoleMapper userRoleMapper,
                     ObjectProvider<AccountProvisioner> accountProvisioner) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.accountProvisioner = accountProvisioner;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 用户注册。
     */
    @Transactional
    public UserResult register(UserRegisterRequest request) {
        SysUserEntity existing = userMapper.selectByUsername(request.username());
        if (existing != null) {
            throw new IllegalArgumentException("用户名已存在: " + request.username());
        }

        SysUserEntity user = new SysUserEntity();
        user.setUsername(request.username());
        user.setTenantId(1L);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus("ENABLED");
        userMapper.insert(user);

        // 默认分配 USER 角色
        SysUserRoleEntity userRole = new SysUserRoleEntity();
        userRole.setUserId(user.getId());
        assignRoleByCode(user.getId(), "USER");

        AccountProvisioner provisioner = accountProvisioner.getIfAvailable();
        if (provisioner != null) {
            provisioner.provisionIndividual(user.getId(), user.getNickname());
        }

        log.info("用户注册成功: username={}", request.username());
        return toResult(user);
    }

    /**
     * 执行assign角色人编码。
     *
     * @param userId 用户 ID
     * @param roleCode 角色编码
     */
    private void assignRoleByCode(Long userId, String roleCode) {
        QueryWrapper<SysRoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("role_code", roleCode);
        SysRoleEntity role = roleMapper.selectOne(wrapper);
        if (role != null) {
            SysUserRoleEntity ur = new SysUserRoleEntity();
            ur.setUserId(userId);
            ur.setRoleId(role.getId());
            userRoleMapper.insert(ur);
        }
    }

    /**
     * 用户登录验证。
     */
    public UserResult login(UserLoginRequest request) {
        SysUserEntity user = userMapper.selectByUsername(request.username());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (!"ENABLED".equals(user.getStatus())) {
            throw new IllegalArgumentException("用户已被禁用");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户登录成功: username={}", request.username());
        return toResult(user);
    }

    /**
     * 根据 ID 查询用户。
     */
    public UserResult getById(Long id) {
        SysUserEntity user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return toResult(user);
    }

    /**
     * 分页查询用户列表。
     */
    public Page<UserResult> listUsers(int current, int size) {
        Page<SysUserEntity> page = new Page<>(current, size);
        userMapper.selectPage(page, new QueryWrapper<SysUserEntity>().orderByDesc("create_time"));

        List<UserResult> records = page.getRecords().stream()
                .map(this::toResult)
                .toList();

        Page<UserResult> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 转换为结果。
     *
     * @param user 用户
     * @return 转换结果
     */
    private UserResult toResult(SysUserEntity user) {
        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
        return new UserResult(
                user.getId(),
                user.getUsername(),
                resolveTenantCode(user.getTenantId()),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getStatus(),
                roles,
                user.getLastLoginTime(),
                user.getCreateTime()
        );
    }

    /**
     * 执行resolve租户编码。
     *
     * @param tenantId 租户 ID
     * @return 执行结果
     */
    private String resolveTenantCode(Long tenantId) {
        if (tenantId == null || tenantId == 1L) {
            return TenantContext.DEFAULT_TENANT_CODE;
        }
        return String.valueOf(tenantId);
    }
}
