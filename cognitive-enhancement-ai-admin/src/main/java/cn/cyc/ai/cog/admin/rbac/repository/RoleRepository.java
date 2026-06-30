package cn.cyc.ai.cog.admin.rbac.repository;

import cn.cyc.ai.cog.admin.rbac.dto.RolePageQuery;
import cn.cyc.ai.cog.admin.rbac.entity.RoleEntity;
import cn.cyc.ai.cog.common.page.PageResult;

import java.util.List;

/**
 * 角色仓储接口。
 *
 * @author cyc
 * @date 2026/6/15 14:18
 */
public interface RoleRepository {

    /**
     * 分页查询角色。
     *
     * @param query 分页与筛选条件
     * @return 角色分页结果
     */
    PageResult<RoleEntity> page(RolePageQuery query);

    /**
     * 查询全部角色。
     *
     * @return 角色列表
     */
    List<RoleEntity> listAll();

    /**
     * 按 ID 查询角色，不存在时抛出业务异常。
     *
     * @param id 角色 ID
     * @return 角色实体
     */
    RoleEntity requireById(Long id);

    /**
     * 校验角色编码是否可用。
     *
     * @param roleCode  角色编码
     * @param excludeId 排除的角色 ID，可为 null
     * @return 是否可用
     */
    boolean isCodeAvailable(String roleCode, Long excludeId);

    /**
     * 新增角色。
     *
     * @param entity 角色实体
     * @return 持久化后的角色
     */
    RoleEntity insert(RoleEntity entity);

    /**
     * 更新角色。
     *
     * @param entity 角色实体
     * @return 更新后的角色
     */
    RoleEntity update(RoleEntity entity);

    /**
     * 删除角色及其权限绑定。
     *
     * @param id 角色 ID
     */
    void deleteById(Long id);

    /**
     * 统计角色下成员数。
     *
     * @param roleId 角色 ID
     * @return 成员数
     */
    long countMembers(Long roleId);

    /**
     * 重新绑定角色权限点。
     *
     * @param roleId        角色 ID
     * @param permissionIds 权限点 ID 列表
     */
    void replacePermissions(Long roleId, List<Long> permissionIds);
}
