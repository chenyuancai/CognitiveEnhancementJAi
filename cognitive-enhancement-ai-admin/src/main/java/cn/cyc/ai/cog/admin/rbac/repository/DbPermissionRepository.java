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
 *
 * @author cyc
 * @date 2026/6/15 14:18
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

    /**
     * 查询All列表。
     * @return 结果列表
     */
    @Override
    public List<PermissionEntity> listAll() {
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                .orderByAsc(PermissionEntity::getSortNo)
                .orderByAsc(PermissionEntity::getId));
    }

    /**
     * 查询Custom列表。
     * @return 结果列表
     */
    @Override
    public List<PermissionEntity> listCustom() {
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getBuiltin, false)
                .orderByDesc(PermissionEntity::getId));
    }

    /**
     * 查询是否启用人Scope列表。
     *
     * @param scope scope
     * @return 结果列表
     */
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

    /**
     * 查询Codes人角色ID列表。
     *
     * @param roleId 角色ID
     * @return 结果列表
     */
    @Override
    public List<String> listCodesByRoleId(Long roleId) {
        return permissionMapper.selectCodesByRoleId(roleId);
    }

    /**
     * 查询人用户ID列表。
     *
     * @param userId 用户 ID
     * @return 结果列表
     */
    @Override
    public List<PermissionEntity> listByUserId(Long userId) {
        return permissionMapper.selectByUserId(userId);
    }

    /**
     * 查询人Codes列表。
     *
     * @param codes codes
     * @return 结果列表
     */
    @Override
    public List<PermissionEntity> listByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectList(new LambdaQueryWrapper<PermissionEntity>()
                .in(PermissionEntity::getPermissionCode, codes));
    }

    /**
     * 判断是否为编码Available。
     *
     * @param code 编码
     * @param excludeId excludeID
     * @return 是否满足条件
     */
    @Override
    public boolean isCodeAvailable(String code, Long excludeId) {
        LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<PermissionEntity>()
                .eq(PermissionEntity::getPermissionCode, code.trim());
        if (excludeId != null) {
            wrapper.ne(PermissionEntity::getId, excludeId);
        }
        return permissionMapper.selectCount(wrapper) == 0;
    }

    /**
     * 执行require人ID。
     *
     * @param id 主键 ID
     * @return 执行结果
     */
    @Override
    public PermissionEntity requireById(Long id) {
        PermissionEntity entity = permissionMapper.selectById(id);
        if (entity == null) {
            throw Errors.of(PlatformErrorCode.PERMISSION_NOT_FOUND, "权限点不存在：" + id);
        }
        return entity;
    }

    /**
     * 执行insert。
     *
     * @param entity 实体
     * @return 执行结果
     */
    @Override
    public PermissionEntity insert(PermissionEntity entity) {
        permissionMapper.insert(entity);
        return entity;
    }

    /**
     * 更新Item。
     *
     * @param entity 实体
     * @return 更新结果
     */
    @Override
    public PermissionEntity update(PermissionEntity entity) {
        permissionMapper.updateById(entity);
        return entity;
    }

    /**
     * 删除人ID。
     *
     * @param id 主键 ID
     */
    @Override
    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }
}
