package cn.cyc.ai.cog.admin.rbac.repository;

import cn.cyc.ai.cog.common.exception.Errors;
import cn.cyc.ai.cog.common.exception.PlatformErrorCode;
import cn.cyc.ai.cog.admin.rbac.dto.RolePageQuery;
import cn.cyc.ai.cog.admin.rbac.entity.RoleEntity;
import cn.cyc.ai.cog.admin.rbac.entity.RolePermissionEntity;
import cn.cyc.ai.cog.admin.rbac.mapper.RoleMapper;
import cn.cyc.ai.cog.admin.rbac.mapper.RolePermissionMapper;
import cn.cyc.ai.cog.common.exception.ServiceException;
import cn.cyc.ai.cog.common.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色仓储 MyBatis 实现。
 */
@Repository
public class DbRoleRepository implements RoleRepository {

    /** 角色 Mapper */
    private final RoleMapper roleMapper;

    /** 角色权限关联 Mapper */
    private final RolePermissionMapper rolePermissionMapper;

    /**
     * @param roleMapper           角色 Mapper
     * @param rolePermissionMapper 角色权限关联 Mapper
     */
    public DbRoleRepository(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public PageResult<RoleEntity> page(RolePageQuery query) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(RoleEntity::getRoleCode, query.getKeyword())
                    .or().like(RoleEntity::getRoleName, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(RoleEntity::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(RoleEntity::getId);
        Page<RoleEntity> page = roleMapper.selectPage(Page.of(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<RoleEntity> listAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>().orderByDesc(RoleEntity::getId));
    }

    @Override
    public RoleEntity requireById(Long id) {
        RoleEntity entity = roleMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.ROLE_NOT_FOUND, "角色不存在：" + id);
        }
        return entity;
    }

    @Override
    public boolean isCodeAvailable(String roleCode, Long excludeId) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, roleCode.trim());
        if (excludeId != null) {
            wrapper.ne(RoleEntity::getId, excludeId);
        }
        return roleMapper.selectCount(wrapper) == 0;
    }

    @Override
    public RoleEntity insert(RoleEntity entity) {
        roleMapper.insert(entity);
        return entity;
    }

    @Override
    public RoleEntity update(RoleEntity entity) {
        roleMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, id));
        roleMapper.deleteById(id);
    }

    @Override
    public long countMembers(Long roleId) {
        return roleMapper.countMembers(roleId);
    }

    @Override
    public void replacePermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, roleId));
        for (Long permissionId : permissionIds) {
            RolePermissionEntity link = new RolePermissionEntity();
            link.setRoleId(roleId);
            link.setPermissionId(permissionId);
            rolePermissionMapper.insert(link);
        }
    }
}
