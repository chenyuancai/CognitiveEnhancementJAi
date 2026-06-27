package cn.cyc.ai.cog.admin.rbac.repository;

import cn.cyc.ai.cog.admin.rbac.entity.PermissionEntity;

import java.util.List;

/**
 * 权限点仓储接口。
 */
public interface PermissionRepository {

    /**
     * 查询全部权限点（平铺）。
     *
     * @return 权限点列表
     */
    List<PermissionEntity> listAll();

    /**
     * 查询自定义权限点。
     *
     * @return 自定义权限点列表
     */
    List<PermissionEntity> listCustom();

    /**
     * 按 scope 查询启用中的权限点。
     *
     * @param scope 作用域，可为 null
     * @return 权限点列表
     */
    List<PermissionEntity> listEnabledByScope(String scope);

    /**
     * 查询角色已绑定的权限点编码。
     *
     * @param roleId 角色 ID
     * @return 权限点编码列表
     */
    List<String> listCodesByRoleId(Long roleId);

    /**
     * 查询用户拥有的全部权限点。
     *
     * @param userId 用户 ID
     * @return 权限点列表
     */
    List<PermissionEntity> listByUserId(Long userId);

    /**
     * 按权限码批量查询。
     *
     * @param codes 权限码列表
     * @return 权限点列表
     */
    List<PermissionEntity> listByCodes(List<String> codes);

    /**
     * 校验权限码是否可用。
     *
     * @param code      权限码
     * @param excludeId 排除的权限 ID，可为 null
     * @return 是否可用
     */
    boolean isCodeAvailable(String code, Long excludeId);

    /**
     * 按 ID 查询权限点，不存在时抛出业务异常。
     *
     * @param id 权限点 ID
     * @return 权限点实体
     */
    PermissionEntity requireById(Long id);

    /**
     * 新增权限点。
     *
     * @param entity 权限点实体
     * @return 持久化后的权限点
     */
    PermissionEntity insert(PermissionEntity entity);

    /**
     * 更新权限点。
     *
     * @param entity 权限点实体
     * @return 更新后的权限点
     */
    PermissionEntity update(PermissionEntity entity);

    /**
     * 删除权限点。
     *
     * @param id 权限点 ID
     */
    void deleteById(Long id);
}
