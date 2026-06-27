package cn.cyc.ai.cog.admin.rbac.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.admin.rbac.entity.PermissionEntity;
import cn.cyc.ai.cog.admin.rbac.mapper.PermissionMapper;
import cn.cyc.ai.cog.common.constant.CommonConstants;
import cn.cyc.ai.cog.common.exception.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 权限点仓储 MyBatis 实现。
 */
@Repository
public class DbPermissionRepository implements PermissionRepository {

    /** 权限点 Mapper */
    private final PermissionMapper permissionMapper;

    /**
     * @param permissionMapper 权限点 Mapper
     */
    public DbPermissionRepository(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<PermissionEntity> listAll() {
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                .orderByAsc(PermissionEntity::getSortNo)
                .orderByAsc(PermissionEntity::getId));
    }

    @Override
    public List<PermissionEntity> listCustom() {
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getBuiltin, false)
                .orderByDesc(PermissionEntity::getId));
    }

    @Override
    public List<PermissionEntity> listEnabledByScope(String scope) {
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getStatus, CommonConstants.STATUS_ENABLED)
                .orderByAsc(PermissionEntity::getSortNo);
        if (StringUtils.hasText(scope)) {
            wrapper.eq(PermissionEntity::getScope, scope);
        }
        return permissionMapper.selectList(wrapper);
    }

    @Override
    public List<String> listCodesByRoleId(Long roleId) {
        return permissionMapper.selectCodesByRoleId(roleId);
    }

    @Override
    public List<PermissionEntity> listByUserId(Long userId) {
        return permissionMapper.selectByUserId(userId);
    }

    @Override
    public List<PermissionEntity> listByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                .in(PermissionEntity::getPermissionCode, codes));
    }

    @Override
    public boolean isCodeAvailable(String code, Long excludeId) {
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getPermissionCode, code.trim());
        if (excludeId != null) {
            wrapper.ne(PermissionEntity::getId, excludeId);
        }
        return permissionMapper.selectCount(wrapper) == 0;
    }

    @Override
    public PermissionEntity requireById(Long id) {
        PermissionEntity entity = permissionMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.PERMISSION_NOT_FOUND, "权限点不存在：" + id);
        }
        return entity;
    }

    @Override
    public PermissionEntity insert(PermissionEntity entity) {
        permissionMapper.insert(entity);
        return entity;
    }

    @Override
    public PermissionEntity update(PermissionEntity entity) {
        permissionMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }
}
