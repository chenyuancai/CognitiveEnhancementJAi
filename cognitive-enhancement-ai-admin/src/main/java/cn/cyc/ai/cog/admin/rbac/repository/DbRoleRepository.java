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
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 执行分页。
     *
     * @param query 查询
     * @return 执行结果
     */
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

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<RoleEntity> listAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<RoleEntity>().orderByDesc(RoleEntity::getId));
    }

    /**
     * 执行require人ID。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Override
    public RoleEntity requireById(Long id) {
        RoleEntity entity = roleMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.ROLE_NOT_FOUND, "角色不存在：" + id);
        }
        return entity;
    }

    /**
     * 判断是否为编码Available。
     *
     * @param roleCode 角色编码
     * @param excludeId excludeID
     * @return 是否满足条件
     */
    @Override
    public boolean isCodeAvailable(String roleCode, Long excludeId) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, roleCode.trim());
        if (excludeId != null) {
            wrapper.ne(RoleEntity::getId, excludeId);
        }
        return roleMapper.selectCount(wrapper) == 0;
    }

    /**
     * 执行insert。
     *
     * @param entity 实体
     * @return 执行结果
     */
    @Override
    public RoleEntity insert(RoleEntity entity) {
        roleMapper.insert(entity);
        return entity;
    }

    /**
     * 更新Item。
     *
     * @param entity 实体
     * @return 更新结果
     */
    @Override
    public RoleEntity update(RoleEntity entity) {
        roleMapper.updateById(entity);
        return entity;
    }

    /**
     * 删除人ID。
     *
     * @param id 主键 ID
     */
    @Override
    public void deleteById(Long id) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermissionEntity>()
                .eq(RolePermissionEntity::getRoleId, id));
        roleMapper.deleteById(id);
    }

    /**
     * 执行数量Members。
     *
     * @param roleId 角色ID
     * @return 执行结果
     */
    @Override
    public long countMembers(Long roleId) {
        return roleMapper.countMembers(roleId);
    }

    /**
     * 执行replacePermissions。
     *
     * @param roleId 角色ID
     * @param permissionIds 权限Ids
     */
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
