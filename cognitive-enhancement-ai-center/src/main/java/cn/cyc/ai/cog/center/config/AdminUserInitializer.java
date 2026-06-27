package cn.cyc.ai.cog.center.config;

import cn.cyc.ai.cog.center.user.SysRoleEntity;
import cn.cyc.ai.cog.center.user.SysRoleMapper;
import cn.cyc.ai.cog.center.user.SysUserEntity;
import cn.cyc.ai.cog.center.user.SysUserMapper;
import cn.cyc.ai.cog.center.user.SysUserRoleEntity;
import cn.cyc.ai.cog.center.user.SysUserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 管理员用户初始化，应用启动时自动创建默认 admin 账号。
 *
 * <p>仅在持久化模式（Flyway 已初始化角色表）下执行；演示元数据由 Flyway V3 负责。
 *
 * @author cyc
 */
@Configuration
@ConditionalOnProperty(name = "cog.persistence.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnProperty(name = "cog.seed.enabled", havingValue = "true", matchIfMissing = true)
public class AdminUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Bean
    ApplicationRunner adminSeedRunner(
            SysUserMapper userMapper,
            SysRoleMapper roleMapper,
            SysUserRoleMapper userRoleMapper
    ) {
        return args -> {
            QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("username", "admin");
            if (userMapper.selectOne(wrapper) != null) {
                log.info("admin 用户已存在，跳过初始化");
                return;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            SysUserEntity admin = new SysUserEntity();
            admin.setUsername("admin");
            admin.setPasswordHash(encoder.encode("user1234"));
            admin.setNickname("管理员");
            admin.setStatus("ENABLED");
            admin.setTenantId(1L);
            userMapper.insert(admin);

            assignRole(userRoleMapper, roleMapper, admin.getId(), "ADMIN");
            assignRole(userRoleMapper, roleMapper, admin.getId(), "USER");

            log.info("admin 用户初始化完成，userId={}", admin.getId());
        };
    }

    private void assignRole(SysUserRoleMapper userRoleMapper, SysRoleMapper roleMapper, Long userId, String roleCode) {
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
}
